import { ListItemButton, ListItemText } from "@mui/material";
import { useNavigate } from "react-router-dom";
import "./GroupItem.css";

function GroupItem({ group }) {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/groups/${group.groupId}/expense`, {
      state: { group },
    });
  };

  return (
    <ListItemButton className="group-row" onClick={handleClick}>
      <ListItemText
        primary={group.groupName}
        secondary={group.groupDescription}
      />
    </ListItemButton>
  );
}

export default GroupItem;
