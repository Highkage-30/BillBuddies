import {
  Box,
  IconButton,
  Typography,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import "./GroupItem.css";

function GroupItem({ group, onDelete, onOpen }) {
  return (
    <Box
      className="group-item"
      sx={{
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        padding: "12px 16px",
        borderBottom: "1px solid #e0e0e0",
        cursor: "pointer",
      }}
      onClick={() => onOpen(group.groupId)}
    >
      {/* LEFT: name + description */}
      <Box>
        <Typography
          sx={{
            fontWeight: 600,
            color: "#212121",
          }}
        >
          {group.groupName}
        </Typography>

        {group.groupDescription && (
          <Typography
            sx={{
              fontSize: "0.85rem",
              color: "#616161",
              marginTop: "2px",
            }}
          >
            {group.groupDescription}
          </Typography>
        )}
      </Box>

      {/* RIGHT: delete */}
      <IconButton
        color="error"
        onClick={(e) => {
          e.stopPropagation(); // 🚫 don't open group
          onDelete(group);
        }}
      >
        <DeleteIcon />
      </IconButton>
    </Box>
  );
}

export default GroupItem;
