import { useEffect, useState } from "react";
import {
  Container,
  Box,
  Typography,
  TextField,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  Button,
  CircularProgress,
  Alert,
} from "@mui/material";
import { useParams, useNavigate } from "react-router-dom";
import { fetchMemberStatement } from "../../../api/memberStatementApi";
import "./MemberDetail.css";

function MemberDetailPage() {
  const { memberId } = useParams();
  const navigate = useNavigate();

  const [memberName, setMemberName] = useState("");
  const [groups, setGroups] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadData();
  }, [memberId]);

  const loadData = async () => {
    try {
      setLoading(true);
      const data = await fetchMemberStatement(memberId);
      setMemberName(data.memberName);
      setGroups(data.groups || []);
    } catch (err) {
      setError(
        err?.response?.data?.message ||
          "Failed to load member data"
      );
    } finally {
      setLoading(false);
    }
  };

  const filteredGroups = groups.filter((g) =>
    g.groupName
      .toLowerCase()
      .includes(search.toLowerCase())
  );

  if (loading) {
    return (
      <Container className="member-loading">
        <CircularProgress />
      </Container>
    );
  }

  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }

  return (
    <Container className="member-detail-container">
      {/* Header */}
      <Box className="member-header">
        <Typography variant="h5">
          Member: {memberName}
        </Typography>

        <TextField
          size="small"
          label="Search Groups"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </Box>

      {/* Table */}
      <Table className="member-table">
        <TableHead>
          <TableRow className="member-table-header">
            <TableCell>Group Name</TableCell>
            <TableCell>Credit</TableCell>
            <TableCell>Debit</TableCell>
            <TableCell>Net Balance</TableCell>
            <TableCell>Action</TableCell>
          </TableRow>
        </TableHead>

        <TableBody>
          {filteredGroups.map((g) => (
            <TableRow key={g.groupId}>
              <TableCell>{g.groupName}</TableCell>
              <TableCell>₹{g.credit}</TableCell>
              <TableCell>₹{g.debit}</TableCell>
              <TableCell
                className={
                  g.balance > 0
                    ? "positive"
                    : g.balance < 0
                    ? "negative"
                    : ""
                }
              >
                ₹{g.balance}
              </TableCell>
              <TableCell>
                <Button
                  size="small"
                  variant="outlined"
                  onClick={() =>
                    navigate(
                      `/groups/${g.groupId}/expense`
                    )
                  }
                >
                  View Expenses
                </Button>
              </TableCell>
            </TableRow>
          ))}

          {filteredGroups.length === 0 && (
            <TableRow>
              <TableCell
                colSpan={5}
                align="center"
              >
                No groups found
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </Container>
  );
}

export default MemberDetailPage;
