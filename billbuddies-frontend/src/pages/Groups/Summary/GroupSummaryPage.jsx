import { useEffect, useState } from "react";
import {
  Container,
  CircularProgress,
  Alert,
  Box,
  Button,
  Snackbar,
  Alert as MuiAlert,
  Typography,
} from "@mui/material";
import DownloadIcon from "@mui/icons-material/Download";
import { useParams } from "react-router-dom";
import SummaryTable from "./SummaryTable";
import {
  fetchGroupSummary,
  downloadGroupSummary,
} from "../../../api/summaryApi";

function GroupSummaryPage() {
  const { groupId } = useParams();

  const [members, setMembers] = useState(null);
  const [groupName, setGroupName] = useState("");
  const [error, setError] = useState(null);
  const [downloading, setDownloading] =
    useState(false);
  const [toast, setToast] = useState(null);

  useEffect(() => {
    let alive = true;

    fetchGroupSummary(groupId)
      .then((res) => {
        if (!alive) return;
        setMembers(res.members);
        setGroupName(res.groupName || "Group");
      })
      .catch((err) => {
        if (!alive) return;
        setError(
          err?.response?.data?.message ||
            "Failed to load summary"
        );
      });

    return () => {
      alive = false;
    };
  }, [groupId]);

  const handleDownload = async () => {
    try {
      setDownloading(true);

      const blob =
        await downloadGroupSummary(groupId);

      const today = new Date()
        .toISOString()
        .split("T")[0];

      const safeGroupName =
        groupName.replace(/\s+/g, "-");

      const filename =
        `${safeGroupName}-${today}.xlsx`;

      const url =
        window.URL.createObjectURL(blob);

      const link =
        document.createElement("a");

      link.href = url;
      link.setAttribute("download", filename);

      document.body.appendChild(link);
      link.click();
      link.remove();

      setToast({
        type: "success",
        msg: "Excel report downloaded successfully",
      });
    } catch (err) {
      setToast({
        type: "error",
        msg:
          err?.response?.data?.message ||
          "Failed to download report",
      });
    } finally {
      setDownloading(false);
    }
  };

  if (error) {
    return (
      <Container>
        <Alert severity="error">{error}</Alert>
      </Container>
    );
  }

  if (members === null) {
    return (
      <Container>
        <CircularProgress />
      </Container>
    );
  }

  const noData =
    !members || members.length === 0;

  return (
    <Container>

      {/* HEADER ROW */}
      <Box
        display="flex"
        justifyContent="space-between"
        alignItems="center"
        mb={2}
      >
        <Typography variant="h6">
          Group Summary
        </Typography>

        <Button
          variant="contained"
          color="success"
          startIcon={
            downloading ? (
              <CircularProgress
                size={18}
                color="inherit"
              />
            ) : (
              <DownloadIcon />
            )
          }
          onClick={handleDownload}
          disabled={downloading || noData}
          sx={{
            textTransform: "none",
            fontWeight: 600,
            boxShadow: 2,
          }}
        >
          {downloading
            ? "Generating..."
            : "Download Report"}
        </Button>
      </Box>

      {/* SUMMARY TABLE */}
      {noData ? (
        <Alert severity="info">
          No summary data available
        </Alert>
      ) : (
        <SummaryTable data={members} />
      )}

      {/* TOAST */}
      {toast && (
        <Snackbar
          open
          autoHideDuration={4000}
          onClose={() => setToast(null)}
        >
          <MuiAlert
            severity={toast.type}
            variant="filled"
          >
            {toast.msg}
          </MuiAlert>
        </Snackbar>
      )}
    </Container>
  );
}

export default GroupSummaryPage;
