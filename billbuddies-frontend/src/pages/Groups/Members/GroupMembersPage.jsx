import { useEffect, useState, useRef } from "react";
import {
  Container,
  Snackbar,
  Alert,
  Button,
  Box,
} from "@mui/material";
import UploadFileIcon from "@mui/icons-material/UploadFile";
import { useParams } from "react-router-dom";
import GroupMemberItem from "./GroupMemberItem";
import AddMembersPanel from "./AddMembersPanel";
import {
  fetchGroupMembers,
  fetchGroupStatement,
  addMembersToGroupBulk,
  uploadGroupMembers,
} from "../../../api/groupMemberApi";
import { fetchMembers } from "../../../api/memberApi";
import "./GroupMembers.css";

function GroupMembersPage() {
  const { groupId } = useParams();

  const [members, setMembers] = useState([]);
  const [availableMembers, setAvailableMembers] = useState([]);
  const [toast, setToast] = useState(null);
  const [uploading, setUploading] = useState(false);

  const fileInputRef = useRef(null);

  useEffect(() => {
    loadAll();
  }, [groupId]);

  const loadAll = async () => {
    try {
      const globalMembers = await fetchMembers();
      const globalNames = globalMembers.map(
        (m) => m.memberName
      );

      const groupMembers =
        await fetchGroupMembers(groupId);
      const groupNames = groupMembers.map(
        (m) => m.memberName
      );

      const statementRes =
        await fetchGroupStatement(groupId);

      const statementMembers =
        statementRes.members || [];

      const balanceMap = {};
      statementMembers.forEach((m) => {
        balanceMap[m.memberName] = m.balance;
      });

      const merged = groupMembers.map((m) => ({
        memberId: m.memberId,
        memberName: m.memberName,
        balance:
          balanceMap[m.memberName] ?? null,
      }));

      const filteredAvailable = globalNames.filter(
        (name) => !groupNames.includes(name)
      );

      setMembers(merged);
      setAvailableMembers(filteredAvailable);
    } catch (err) {
      setToast({
        type: "error",
        msg:
          err?.response?.data?.message ||
          "Failed to load group members",
      });
    }
  };

  /* ================= ADD MEMBERS ================= */

  const handleAddMembers = async (names) => {
    try {
      if (!Array.isArray(names) || names.length === 0)
        return;

      await addMembersToGroupBulk(groupId, names);

      setToast({
        type: "success",
        msg: "Members added successfully",
      });

      loadAll();
    } catch (err) {
      setToast({
        type: "error",
        msg:
          err?.response?.data?.message ||
          "Failed to add members",
      });
    }
  };

  /* ================= UPLOAD MEMBERS ================= */

  const handleUploadClick = () => {
    fileInputRef.current.click();
  };

  const handleFileChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    try {
      setUploading(true);

      await uploadGroupMembers(groupId, file);

      setToast({
        type: "success",
        msg: "Members uploaded successfully",
      });

      loadAll();
    } catch (err) {
      setToast({
        type: "error",
        msg:
          err?.response?.data?.message ||
          "Upload failed",
      });
    } finally {
      setUploading(false);
      e.target.value = null;
    }
  };

  return (
  <Container className="group-members-container">

    {/* ===== MEMBERS LIST ===== */}
    {members.map((m) => (
      <GroupMemberItem
        key={m.memberId}
        member={m}
      />
    ))}

    {/* ===== ACTION SECTION ===== */}
    <Box mt={4}>

      {/* Upload Button */}
      <Box mb={2}>
        <Button
          variant="outlined"
          startIcon={<UploadFileIcon />}
          onClick={handleUploadClick}
          disabled={uploading}
        >
          {uploading ? "Uploading..." : "Upload Members"}
        </Button>

        <input
          type="file"
          accept=".csv,.xlsx"
          ref={fileInputRef}
          style={{ display: "none" }}
          onChange={handleFileChange}
        />
      </Box>

      {/* Add Members Panel (kept full width) */}
      <AddMembersPanel
        options={availableMembers}
        onSubmit={handleAddMembers}
      />

    </Box>

    {toast && (
      <Snackbar
        open
        autoHideDuration={4000}
        onClose={() => setToast(null)}
      >
        <Alert severity={toast.type} variant="filled">
          {toast.msg}
        </Alert>
      </Snackbar>
    )}
  </Container>
);
}

export default GroupMembersPage;
