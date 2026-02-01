import {
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
} from "@mui/material";

function SummaryTable({ data }) {
  const getBalanceStyle = (value) => {
    if (value > 0) return { color: "#2e7d32", fontWeight: 600 };
    if (value < 0) return { color: "#d32f2f", fontWeight: 600 };
    return { color: "#616161", fontWeight: 600 };
  };

  const getStatus = (value) => {
    if (value > 0)
      return { label: "Will Receive", color: "#2e7d32" };
    if (value < 0)
      return { label: "Needs to Pay", color: "#d32f2f" };
    return { label: "Settled", color: "#616161" };
  };

  return (
    <Table className="summary-table">
      <TableHead>
        <TableRow className="summary-header-row">
          <TableCell className="summary-header-cell">
            Member
          </TableCell>
          <TableCell className="summary-header-cell">
            Total Received
          </TableCell>
          <TableCell className="summary-header-cell">
            Total Paid
          </TableCell>
          <TableCell className="summary-header-cell">
            Net Balance
          </TableCell>
          <TableCell className="summary-header-cell">
            Status
          </TableCell>
        </TableRow>
      </TableHead>

      <TableBody>
        {data.map((row) => {
          const status = getStatus(row.balance);

          return (
            <TableRow
              key={row.memberName}
              className="summary-body-row"
            >
              <TableCell className="summary-cell">
                {row.memberName}
              </TableCell>

              <TableCell
                className="summary-cell"
                sx={{ color: "#2e7d32" }}
              >
                ₹{row.credit}
              </TableCell>

              <TableCell
                className="summary-cell"
                sx={{ color: "#d32f2f" }}
              >
                ₹{row.debit}
              </TableCell>

              <TableCell
                className="summary-cell"
                sx={getBalanceStyle(row.balance)}
              >
                ₹{row.balance}
              </TableCell>

              <TableCell
                className="summary-cell"
                sx={{ color: status.color, fontWeight: 600 }}
              >
                {status.label}
              </TableCell>
            </TableRow>
          );
        })}
      </TableBody>
    </Table>
  );
}

export default SummaryTable;
