import { useEffect, useState } from "react";
import { Container, Snackbar, Alert } from "@mui/material";
import { useNavigate } from "react-router-dom";
import "./AddGroupPage.css";
import { fetchMembers } from "../../../api/memberApi";
import { createGroup } from "../../../api/groupApi";
import { uploadGroupMembers } from "../../../api/groupMemberApi";
import AddGroupForm from "./AddGroupForm";

function AddGroupPage() {
  const navigate = useNavigate();

  const [groupName, setGroupName] = useState("");
  const [groupDescription, setGroupDescription] = useState("");
  const [members, setMembers] = useState([]);
  const [selectedMembers, setSelectedMembers] = useState([]);
  const [uploadFile, setUploadFile] = useState(null);

  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [toast, setToast] = useState({
    open: false,
    message: "",
    severity: "success",
  });

  useEffect(() => {
    fetchMembers()
      .then((res) => setMembers(res.map((m) => m.memberName)))
      .catch(() => showToast("Failed to load members", "error"));
  }, []);

  const validate = () => {
    const e = {};
    if (!groupName.trim()) e.groupName = "Group name is required";
    if (selectedMembers.length === 0 && !uploadFile)
      e.members = "Add members manually or upload file";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleMembersChange = (values) => {
    const cleaned = values.map((v) =>
      v.startsWith('Add "') ? v.replace(/^Add "|"$|"/g, "") : v
    );
    setSelectedMembers([...new Set(cleaned)]);
    setErrors((p) => ({ ...p, members: null }));
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const allowed = [
      "text/csv",
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      "application/vnd.ms-excel",
    ];

    if (!allowed.includes(file.type)) {
      showToast("Only CSV or Excel allowed", "error");
      return;
    }

    setUploadFile(file);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    try {
      setLoading(true);

      // 1️⃣ Create group
      const res = await createGroup({
        groupName,
        groupDescription,
        memberList: selectedMembers,
      });

      const newGroupId = res.groupId;

      // 2️⃣ Upload members if file exists
      if (uploadFile) {
        await uploadGroupMembers(newGroupId, uploadFile);
      }

      showToast("Group created successfully", "success");

      setTimeout(() => navigate("/groups"), 800);
    } catch (err) {
      showToast(
        err?.response?.data?.message || "Failed to create group",
        "error"
      );
    } finally {
      setLoading(false);
    }
  };

  const showToast = (message, severity) => {
    setToast({ open: true, message, severity });
  };

  return (
    <Container className="add-group-page">
      <AddGroupForm
        groupName={groupName}
        groupDescription={groupDescription}
        members={members}
        selectedMembers={selectedMembers}
        errors={errors}
        loading={loading}
        uploadFile={uploadFile}
        onFileChange={handleFileChange}
        onGroupNameChange={(e) => setGroupName(e.target.value)}
        onGroupDescriptionChange={(e) => setGroupDescription(e.target.value)}
        onMembersChange={handleMembersChange}
        onSubmit={handleSubmit}
      />

      <Snackbar
        open={toast.open}
        autoHideDuration={3000}
        onClose={() => setToast({ ...toast, open: false })}
      >
        <Alert severity={toast.severity} variant="filled">
          {toast.message}
        </Alert>
      </Snackbar>
    </Container>
  );
}

export default AddGroupPage;
