import { TableRow, TableCell } from "@mui/material";

function SummaryRow({ row }) {
  const getBalanceStyle = (value) => {
    if (value > 0)
      return { color: "#2e7d32", fontWeight: 600 };
    if (value < 0)
      return { color: "#d32f2f", fontWeight: 600 };
    return { color: "#616161", fontWeight: 600 };
  };

  const getStatus = (value) => {
    if (value > 0)
      return { label: "Will Receive", color: "#2e7d32" };
    if (value < 0)
      return { label: "Needs to Pay", color: "#d32f2f" };
    return { label: "Settled", color: "#616161" };
  };

  const status = getStatus(row.balance);

  return (
    <TableRow>
      <TableCell sx={cellStyle}>
        {row.memberName}
      </TableCell>

      <TableCell
        sx={{
          ...cellStyle,
          color: "#2e7d32",
        }}
      >
        ₹ {row.credit}
      </TableCell>

      <TableCell
        sx={{
          ...cellStyle,
          color: "#d32f2f",
        }}
      >
        ₹ {row.debit}
      </TableCell>

      <TableCell
        sx={{
          ...cellStyle,
          ...getBalanceStyle(row.balance),
        }}
      >
        ₹ {row.balance}
      </TableCell>

      <TableCell
        sx={{
          ...cellStyle,
          color: status.color,
          fontWeight: 600,
        }}
      >
        {status.label}
      </TableCell>
    </TableRow>
  );
}

const cellStyle = {
  borderRight: "1px solid #e0e0e0",
  borderBottom: "1px solid #e0e0e0",
};

export default SummaryRow;
