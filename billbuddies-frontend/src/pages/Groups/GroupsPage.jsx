import { useEffect, useState } from "react";
import {
  Container,
  Typography,
  TextField,
  Box,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Snackbar,
  Alert,
} from "@mui/material";
import "./GroupsPage.css";
import GroupList from "./components/GroupList";
import {
  fetchGroups,
  deleteGroup,
} from "../../api/groupApi";
import { useNavigate } from "react-router-dom";

function GroupsPage() {
  const navigate = useNavigate();

  const [groups, setGroups] = useState([]);
  const [loading, setLoading] = useState(true);
  const [hasError, setHasError] = useState(false);
  const [search, setSearch] = useState("");

  // delete flow
  const [deleteTarget, setDeleteTarget] =
    useState(null);
  const [toast, setToast] = useState(null);

  useEffect(() => {
    loadGroups();
  }, []);

  const loadGroups = async () => {
    try {
      setLoading(true);
      const data = await fetchGroups();
      setGroups(data);
      setHasError(false);
    } catch {
      setHasError(true);
    } finally {
      setLoading(false);
    }
  };

  const filteredGroups = groups.filter((group) =>
    group.groupName
      .toLowerCase()
      .includes(search.toLowerCase())
  );

  /* ---------------- DELETE FLOW ---------------- */

  const requestDelete = (group) => {
    setDeleteTarget(group);
  };

  const confirmDelete = async () => {
    if (!deleteTarget) return;

    const groupId = deleteTarget.groupId;

    // optimistic remove
    setGroups((prev) =>
      prev.filter((g) => g.groupId !== groupId)
    );

    setDeleteTarget(null);

    try {
      await deleteGroup(groupId);

      setToast({
        type: "success",
        msg: "Group deleted successfully",
      });
    } catch (err) {
      // rollback by reload
      await loadGroups();

      setToast({
        type: "error",
        msg:
          err?.response?.data?.message ||
          "Failed to delete group",
      });
    }
  };

  if (loading)
    return (
      <p className="groups-status">
        Loading groups...
      </p>
    );

  if (hasError)
    return (
      <p className="groups-error">
        Unable to load groups. Please try again later.
      </p>
    );

  return (
    <Container className="groups-page">
      <div className="groups-panel">
        {/* Header */}
        <Box className="groups-header">
          <Typography variant="h5">
            Groups
          </Typography>

          <TextField
            size="small"
            placeholder="Search groups"
            value={search}
            onChange={(e) =>
              setSearch(e.target.value)
            }
          />
        </Box>

        {/* Body */}
        <div className="groups-body">
          {filteredGroups.length === 0 && search ? (
            <p className="groups-empty">
              No groups found matching{" "}
              <strong>"{search}"</strong>
            </p>
          ) : (
            <GroupList
              groups={filteredGroups}
              onDelete={requestDelete}
              onOpen={(id) =>
                navigate(`/groups/${id}`)
              }
            />
          )}
        </div>

        {/* Footer */}
        <Box className="groups-footer">
          <Button
            variant="contained"
            onClick={() => navigate("/groups/new")}
          >
            Add Group
          </Button>
        </Box>
      </div>

      {/* ✅ CONFIRM DELETE MODAL */}
      <Dialog
        open={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
      >
        <DialogTitle>
          Delete Group
        </DialogTitle>
        <DialogContent>
          Are you sure you want to delete{" "}
          <strong>
            {deleteTarget?.groupName}
          </strong>
          ?
          <br />
          <br />
          <em>
            This will permanently remove all group
            data.
          </em>
        </DialogContent>
        <DialogActions>
          <Button
            onClick={() => setDeleteTarget(null)}
          >
            Cancel
          </Button>
          <Button
            color="error"
            variant="contained"
            onClick={confirmDelete}
          >
            Delete
          </Button>
        </DialogActions>
      </Dialog>

      {/* 🔔 TOAST */}
      {toast && (
        <Snackbar
          open
          autoHideDuration={4000}
          onClose={() => setToast(null)}
        >
          <Alert
            severity={toast.type}
            variant="filled"
          >
            {toast.msg}
          </Alert>
        </Snackbar>
      )}
    </Container>
  );
}

export default GroupsPage;
