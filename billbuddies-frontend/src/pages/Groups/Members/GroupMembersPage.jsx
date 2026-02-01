import { useEffect, useState } from "react";
import {
  Container,
  Snackbar,
  Alert,
} from "@mui/material";
import { useParams } from "react-router-dom";
import GroupMemberItem from "./GroupMemberItem";
import AddMembersPanel from "./AddMembersPanel";
import {
  fetchGroupMembers,
  fetchGroupStatement,
  addMemberToGroup,
} from "../../../api/groupMemberApi";
import { fetchMembers } from "../../../api/memberApi";
import "./GroupMembers.css";

function GroupMembersPage() {
  const { groupId } = useParams();

  const [members, setMembers] = useState([]);
  const [availableMembers, setAvailableMembers] =
    useState([]);
  const [toast, setToast] = useState(null);

  useEffect(() => {
    loadAll();
  }, [groupId]);

  const loadAll = async () => {
    try {
      // 1️⃣ Fetch global members
      const globalMembers = await fetchMembers();
      const globalNames = globalMembers.map(
        (m) => m.memberName
      );

      // 2️⃣ Fetch group members
      const groupMembers =
        await fetchGroupMembers(groupId);
      const groupNames = groupMembers.map(
        (m) => m.memberName
      );

      // 3️⃣ Fetch statement
      const statement =
        await fetchGroupStatement(groupId);

      const statementMap = {};
      statement.forEach((s) => {
        statementMap[s.memberName] = s.balance;
      });

      // 4️⃣ Merge group members + balance
      const merged = groupMembers.map((m) => ({
        memberName: m.memberName,
        balance:
          statementMap[m.memberName] ?? null,
      }));

      // 5️⃣ Compute available members (IMPORTANT)
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

  const handleAddMembers = async (names) => {
    try {
      for (const name of names) {
        await addMemberToGroup(groupId, {
          memberName: name,
        });
      }

      setToast({
        type: "success",
        msg: "Members added successfully",
      });

      loadAll(); // refresh lists
    } catch (err) {
      setToast({
        type: "error",
        msg:
          err?.response?.data?.message ||
          "Failed to add members",
      });
    }
  };

  return (
    <Container className="group-members-container">
      {/* Existing group members */}
      {members.map((m) => (
        <GroupMemberItem
          key={m.memberName}
          member={m}
        />
      ))}

      {/* Add new members */}
      <AddMembersPanel
        options={availableMembers}
        onSubmit={handleAddMembers}
      />

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
