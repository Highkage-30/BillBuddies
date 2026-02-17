import {
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
} from "@mui/material";
import SummaryRow from "./SummaryRow";

function SummaryTable({ data }) {
  return (
    <Table
      sx={{
        border: "1px solid #bdbdbd",
        borderCollapse: "collapse",
      }}
    >
      <TableHead>
        <TableRow
          sx={{
            backgroundColor: "#f5f5f5",
          }}
        >
          <TableCell sx={headerCellStyle}>
            Member
          </TableCell>
          <TableCell sx={headerCellStyle}>
            Total Received
          </TableCell>
          <TableCell sx={headerCellStyle}>
            Total Paid
          </TableCell>
          <TableCell sx={headerCellStyle}>
            Net Balance
          </TableCell>
          <TableCell sx={headerCellStyle}>
            Status
          </TableCell>
        </TableRow>
      </TableHead>

      <TableBody>
        {data.map((row) => (
          <SummaryRow
            key={row.memberId}
            row={row}
          />
        ))}
      </TableBody>
    </Table>
  );
}

const headerCellStyle = {
  fontWeight: 600,
  color: "#212121",
  borderRight: "1px solid #bdbdbd",
};

export default SummaryTable;
