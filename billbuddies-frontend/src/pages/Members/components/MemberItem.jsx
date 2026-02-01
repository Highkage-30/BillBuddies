import { ListItemButton, ListItemText } from "@mui/material";
import { useNavigate } from "react-router-dom";
import "./MemberItem.css";

function MemberItem({ memberId, name }) {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/members/${memberId}`);
  };

  return (
    <ListItemButton className="member-row" onClick={handleClick}>
      <ListItemText
        primary={name}
        primaryTypographyProps={{
          fontSize: "15.5px",
          fontWeight: 500,
        }}
      />
    </ListItemButton>
  );
}

export default MemberItem;
