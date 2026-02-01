import { useEffect, useState } from "react";
import { Outlet, useLocation, useParams } from "react-router-dom";
import { Typography, CircularProgress } from "@mui/material";
import GroupSidebar from "./GroupSidebar";
import GroupHeader from "./GroupHeader";
import { fetchGroups } from "../../../api/groupApi";
import "./GroupLayout.css";

function GroupLayout() {
  const { groupId } = useParams();
  const location = useLocation();

  const [group, setGroup] = useState(location.state?.group || null);
  const [loading, setLoading] = useState(!location.state?.group);
  const [error, setError] = useState(false);

  useEffect(() => {
    if (!group) {
      loadGroupFromList();
    }
  }, [groupId]);

  const loadGroupFromList = async () => {
    try {
      const groups = await fetchGroups();
      const found = groups.find(
        (g) => g.groupId === Number(groupId)
      );

      if (!found) throw new Error();
      setGroup(found);
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <CircularProgress />;

  if (error || !group) {
    return (
      <Typography color="error">
        Group not found
      </Typography>
    );
  }

  return (
    <div className="group-layout">
      <GroupSidebar />

      <div className="group-content">
        <GroupHeader
          groupName={group.groupName}
          groupDescription={group.groupDescription}
        />

        <Outlet context={{ group }} />
      </div>

      <div className="group-rightbar"></div>
    </div>
  );
}

export default GroupLayout;
