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
  addMembersToGroupBulk,
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
      /* 1️⃣ Global members */
      const globalMembers = await fetchMembers(); // [{id,name}]
      const globalNames = globalMembers.map(
        (m) => m.memberName
      );

      /* 2️⃣ Group members */
      const groupMembers =
        await fetchGroupMembers(groupId); // [{id,name}]
      const groupNames = groupMembers.map(
        (m) => m.memberName
      );

      /* 3️⃣ Statement (IMPORTANT FIX) */
      const statementRes =
        await fetchGroupStatement(groupId);

      const statementMembers =
        statementRes.members || [];

      const balanceMap = {};
      statementMembers.forEach((m) => {
        balanceMap[m.memberName] = m.balance;
      });

      /* 4️⃣ Merge members + balance */
      const merged = groupMembers.map((m) => ({
        memberId: m.memberId,
        memberName: m.memberName,
        balance:
          balanceMap[m.memberName] ?? null,
      }));

      /* 5️⃣ Available members */
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
      if (!Array.isArray(names) || names.length === 0) {
        return;
      }

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


  return (
    <Container className="group-members-container">
      {/* Existing members */}
      {members.map((m) => (
        <GroupMemberItem
          key={m.memberId}
          member={m}
        />
      ))}

      {/* Add members */}
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
