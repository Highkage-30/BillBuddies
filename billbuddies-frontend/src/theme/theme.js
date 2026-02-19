import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    primary: {
      main: "#1976d2", // matches your existing blue
    },
    secondary: {
      main: "#9c27b0",
    },
  },
  typography: {
    fontFamily: "Roboto, Arial, sans-serif",
    h1: {
      fontSize: "2.25rem",
      fontWeight: 600,
    },
    h6: {
      fontWeight: 600,
    },
  },
});

export default theme;
