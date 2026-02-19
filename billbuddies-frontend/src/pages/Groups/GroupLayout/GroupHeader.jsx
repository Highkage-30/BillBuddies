import { Typography } from "@mui/material";
import "./GroupLayout.css";

function GroupHeader({ groupName, groupDescription }) {
  return (
    <div className="group-header">
      <Typography variant="h6" className="group-title">
        {groupName}
      </Typography>

      <Typography
        variant="body2"
        color="text.secondary"
        className="group-description"
      >
        {groupDescription}
      </Typography>
    </div>
  );
}

export default GroupHeader;
