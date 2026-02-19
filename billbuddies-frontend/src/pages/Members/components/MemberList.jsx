import { List } from "@mui/material";
import MemberItem from "./MemberItem";
import "./MemberList.css";

function MemberList({ members }) {
  return (
    <List className="member-list">
      {members.map((member) => (
        <MemberItem
          key={member.memberId}
          memberId={member.memberId}
          name={member.memberName}
        />
      ))}
    </List>
  );
}

export default MemberList;
