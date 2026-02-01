import { Box, Chip } from "@mui/material";

function GroupMemberItem({ member }) {
  const getStatus = () => {
    if (member.memberName === "BillBuddy") {
      return (
        <Chip
          label="CCP / System"
          color="info"
          variant="outlined"
        />
      );
    }

    if (member.balance === null) {
      return <Chip label="No activity yet" />;
    }

    if (member.balance > 0) {
      return (
        <Chip
          label={`Will Receive ₹${member.balance}`}
          color="success"
        />
      );
    }

    if (member.balance < 0) {
      return (
        <Chip
          label={`Needs to Pay ₹${Math.abs(
            member.balance
          )}`}
          color="error"
        />
      );
    }

    return <Chip label="Settled" />;
  };

  return (
    <Box className="group-member-item">
      <span className="member-name">
        {member.memberName}
      </span>
      {getStatus()}
    </Box>
  );
}

export default GroupMemberItem;
