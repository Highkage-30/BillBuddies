import { useEffect, useState } from "react";
import {
  Container,
  CircularProgress,
  Alert,
} from "@mui/material";
import { useParams } from "react-router-dom";
import SummaryTable from "./SummaryTable";
import { fetchGroupStatement } from "../../../api/statementApi";
import "./Summary.css";

function GroupSummaryPage() {
  const { groupId } = useParams();

  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadSummary();
  }, [groupId]);

  const loadSummary = async () => {
    try {
      const response = await fetchGroupStatement(groupId);
      setData(response);
    } catch (err) {
      setError(
        err?.response?.data?.message ||
          "Failed to load group summary"
      );
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Container className="summary-loading">
        <CircularProgress />
      </Container>
    );
  }

  if (error) {
    return (
      <Alert severity="error">
        {error}
      </Alert>
    );
  }

  if (data.length === 0) {
    return (
      <Alert severity="info">
        No summary available for this group.
      </Alert>
    );
  }

  return (
    <Container className="summary-container">
      <SummaryTable data={data} />
    </Container>
  );
}

export default GroupSummaryPage;
