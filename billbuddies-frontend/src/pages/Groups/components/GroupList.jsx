import { List } from "@mui/material";
import GroupItem from "./GroupItem";
import "./GroupList.css";

function GroupList({ groups }) {
  return (
    <List className="group-list">
      {groups.map((group) => (
        <GroupItem
          key={group.groupId}
          group={group}
        />
      ))}
    </List>
  );
}

export default GroupList;
