import { AppBar, Toolbar, Typography, Button, Box } from "@mui/material";
import { useNavigate, useLocation } from "react-router-dom";
import "./Navbar.css";

function Navbar() {
  const navigate = useNavigate();
  const location = useLocation();

  const isActive = (path) => location.pathname === path;

  return (
    <AppBar position="static">
      <Toolbar className="navbar-toolbar">
        <Typography
          variant="h6"
          component="button"
          onClick={() => navigate("/")}
          className="navbar-logo"
        >
          BillBuddy
        </Typography>

        <Box className="navbar-actions">
          <Button
            color="inherit"
            onClick={() => navigate("/members")}
            className={isActive("/members") ? "active-nav" : ""}
          >
            Members
          </Button>

          <Button
            color="inherit"
            onClick={() => navigate("/groups")}
            className={isActive("/groups") ? "active-nav" : ""}
          >
            Groups
          </Button>
        </Box>
      </Toolbar>
    </AppBar>
  );
}

export default Navbar;
