import {
  Container,
  Typography,
  Box,
  Grid,
  Button,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import SwapHorizIcon from "@mui/icons-material/SwapHoriz";
import AccountBalanceIcon from "@mui/icons-material/AccountBalance";
import InsightsIcon from "@mui/icons-material/Insights";
import "./HomePage.css";

function HomePage() {
  const navigate = useNavigate();
  return (
    <Container className="home-page">
      <Box className="home-hero">
        <Typography variant="h3" className="home-title">
          BillBuddy 💸
        </Typography>
        <Typography className="home-subtitle">
          A smart expense settlement system that simplifies group expenses using
          real financial clearing concepts like <strong>novation</strong> and{" "}
          <strong>netting</strong>, powered by a Central Counter Party —
          <strong> BillBuddy</strong>.
        </Typography>
        <Box className="home-hero-actions">
          <Button
            variant="contained"
            size="large"
            onClick={() => navigate("/members")}
          >
            Explore Members
          </Button>
          <Button
            variant="outlined"
            size="large"
            onClick={() => navigate("/groups")}
          >
            Explore Groups
          </Button>
        </Box>
      </Box>
      <Box className="home-section">
        <Typography variant="h5" gutterBottom>
          Why BillBuddy?
        </Typography>
        <Grid container spacing={4}>
          <Grid item xs={12} md={4}>
            <Box className="home-feature">
              <SwapHorizIcon className="home-feature-icon" />
              <Typography className="home-feature-title">
                Fewer Transactions
              </Typography>
              <Typography className="home-feature-text">
                Eliminates unnecessary peer-to-peer settlements by routing
                obligations through a central entity.
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
                Netting logic computes a single payable or receivable amount per
                member.
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} md={4}>
            <Box className="home-feature">
              <InsightsIcon className="home-feature-icon" />
              <Typography className="home-feature-title">
                Financially Sound
              </Typography>
              <Typography className="home-feature-text">
                Uses novation and netting concepts applied in banking and capital
                markets.
              </Typography>
            </Box>
          </Grid>
        </Grid>
      </Box>
      <Box className="home-section home-muted">
        <Typography variant="h5" gutterBottom>
          Core Concepts
        </Typography>
        <Typography className="home-paragraph">
          <strong>Novation:</strong> All inter-member obligations are transferred
          to a Central Counter Party (BillBuddy), ensuring members don’t owe each
          other directly.
        </Typography>
        <Typography className="home-paragraph">
          <strong>Netting:</strong> Multiple expenses are offset to compute a
          single net balance per member, reducing complexity and settlements.
        </Typography>
      </Box>
      <Box className="home-section">
        <Typography variant="h5" gutterBottom>
          How It Works
        </Typography>
        <ol className="home-steps">
          <li>Create a group</li>
          <li>Add members</li>
          <li>Record expenses</li>
          <li>Expenses are novated to BillBuddy</li>
          <li>Netting computes final balances</li>
          <li>Members view statements and settle dues</li>
        </ol>
      </Box>
    </Container>
  );
}

export default HomePage;
