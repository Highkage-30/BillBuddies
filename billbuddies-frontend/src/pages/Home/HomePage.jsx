import {
  Container,
  Typography,
  Box,
  Grid,
  Button,
  Paper,
  Divider,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import SwapHorizIcon from "@mui/icons-material/SwapHoriz";
import AccountBalanceIcon from "@mui/icons-material/AccountBalance";
import InsightsIcon from "@mui/icons-material/Insights";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import AssessmentIcon from "@mui/icons-material/Assessment";
import PoolIcon from "@mui/icons-material/WaterDrop";
import "./HomePage.css";

function HomePage() {
  const navigate = useNavigate();

  return (
    <Container className="home-page">

      {/* ================= HERO ================= */}

      <Box className="home-hero">
        <Typography variant="h3" className="home-title">
          BillBuddy 💸
        </Typography>

        <Typography className="home-subtitle">
          A ledger-driven expense settlement system built for clarity,
          accuracy, and minimal transactions. Designed for trips,
          roommates, teams, and real-world finance management.
        </Typography>

        <Box className="home-hero-actions">
          <Button
            variant="contained"
            size="large"
            onClick={() => navigate("/groups")}
          >
            Manage Groups
          </Button>

          <Button
            variant="outlined"
            size="large"
            onClick={() => navigate("/members")}
          >
            View Members
          </Button>
        </Box>
      </Box>

      <Divider sx={{ my: 6 }} />

      {/* ================= QUICK ACCESS ================= */}

      <Box className="home-section">
        <Typography variant="h5" gutterBottom>
          Quick Actions
        </Typography>

        <Grid container spacing={3}>
          <Grid item xs={12} md={4}>
            <Paper className="home-card" onClick={() => navigate("/groups")}>
              <SwapHorizIcon className="home-card-icon" />
              <Typography variant="h6">Start a Group</Typography>
              <Typography>
                Create a new trip or project group and begin tracking expenses.
              </Typography>
            </Paper>
          </Grid>

          <Grid item xs={12} md={4}>
            <Paper className="home-card">
              <CloudUploadIcon className="home-card-icon" />
              <Typography variant="h6">Upload Expenses</Typography>
              <Typography>
                Import CSV or Excel files with bulk expense entries.
              </Typography>
            </Paper>
          </Grid>

          <Grid item xs={12} md={4}>
            <Paper className="home-card">
              <AssessmentIcon className="home-card-icon" />
              <Typography variant="h6">Download Reports</Typography>
              <Typography>
                Generate settlement simulation and financial summary Excel reports.
              </Typography>
            </Paper>
          </Grid>
        </Grid>
      </Box>

      <Divider sx={{ my: 6 }} />

      {/* ================= WHY BILLBUDDY ================= */}

      <Box className="home-section">
        <Typography variant="h5" gutterBottom>
          Why BillBuddy?
        </Typography>

        <Grid container spacing={4}>
          <Grid item xs={12} md={4}>
            <Box className="home-feature">
              <SwapHorizIcon className="home-feature-icon" />
              <Typography className="home-feature-title">
                Minimal Settlements
              </Typography>
              <Typography className="home-feature-text">
                Automatically computes the smallest possible set of payments
                between members.
              </Typography>
            </Box>
          </Grid>

          <Grid item xs={12} md={4}>
            <Box className="home-feature">
              <AccountBalanceIcon className="home-feature-icon" />
              <Typography className="home-feature-title">
                Clear Balances
              </Typography>
              <Typography className="home-feature-text">
                Every credit and debit is traceable in a single immutable ledger.
              </Typography>
            </Box>
          </Grid>

          <Grid item xs={12} md={4}>
            <Box className="home-feature">
              <PoolIcon className="home-feature-icon" />
              <Typography className="home-feature-title">
                BillBuddy Pool
              </Typography>
              <Typography className="home-feature-text">
                Track common pool money separately and distribute remaining balance
                fairly at trip closure.
              </Typography>
            </Box>
          </Grid>
        </Grid>
      </Box>

      <Divider sx={{ my: 6 }} />

      {/* ================= HOW IT WORKS ================= */}

      <Box className="home-section home-muted">
        <Typography variant="h5" gutterBottom>
          How It Works
        </Typography>

        <ol className="home-steps">
          <li>Create a group and add members</li>
          <li>Add expenses (member-to-member or vendor payments)</li>
          <li>Deposit or withdraw from BillBuddy pool if needed</li>
          <li>Preview optimized settlement plan</li>
          <li>Execute settlements</li>
          <li>Generate full Excel settlement report</li>
        </ol>
      </Box>

      <Divider sx={{ my: 6 }} />

      {/* ================= SYSTEM PHILOSOPHY ================= */}

      <Box className="home-section">
        <Typography variant="h5" gutterBottom>
          Built on Financial Truth
        </Typography>

        <Typography className="home-paragraph">
          <strong>Ledger-Based Accounting:</strong> Every action —
          expense, deposit, withdrawal, settlement — is recorded
          as immutable ledger entries.
        </Typography>

        <Typography className="home-paragraph">
          <strong>Deterministic Simulation:</strong> Reports simulate
          complete settlement including BillBuddy pool without
          mutating live data.
        </Typography>

        <Typography className="home-paragraph">
          <strong>Production-Grade Transparency:</strong> No hidden
          calculations. Every number derives from a single financial source of truth.
        </Typography>
      </Box>

    </Container>
  );
}

export default HomePage;
