import { useEffect, useState } from "react";
import {
  Container,
  Typography,
  TextField,
  Box,
  Button,
} from "@mui/material";
import "./GroupsPage.css";
import GroupList from "./components/GroupList";
import { fetchGroups } from "../../api/groupApi";
import { useNavigate } from "react-router-dom";

function GroupsPage() {
  const navigate = useNavigate();
  const [groups, setGroups] = useState([]);
  const [loading, setLoading] = useState(true);
  const [hasError, setHasError] = useState(false);
  const [search, setSearch] = useState("");

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
    group.groupName.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) return <p className="groups-status">Loading groups...</p>;
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
          <Typography variant="h5">Groups</Typography>

          <TextField
            size="small"
            placeholder="Search groups"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </Box>

        {/* BODY (flex-grow area) */}
        <div className="groups-body">
          {filteredGroups.length === 0 && search ? (
            <p className="groups-empty">
              No groups found matching <strong>"{search}"</strong>
            </p>
          ) : (
            <GroupList groups={filteredGroups} />
          )}
        </div>

        {/* FOOTER (fixed at bottom) */}
        <Box className="groups-footer">
          <Button variant="contained" onClick={()=>navigate("/groups/new")}>
            Add Group
          </Button>
        </Box>
      </div>
    </Container>
  );
}

export default GroupsPage;
