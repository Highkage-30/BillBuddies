import { Box, Button, Typography } from "@mui/material";
import DownloadIcon from "@mui/icons-material/Download";

function ExpenseTemplatePanel() {
  return (
    <Box p={2}>
      <Typography
        variant="subtitle1"
        fontWeight={600}
        gutterBottom
      >
        Expense Upload Template Files -
      </Typography>

      <Box display="flex" flexDirection="column" gap={1}>
        <Button
          variant="outlined"
          startIcon={<DownloadIcon />}
          component="a"
          href="/templates/expense-template-excel.xlsx"
          download
        >
          Download Excel
        </Button>

        <Button
          variant="outlined"
          startIcon={<DownloadIcon />}
          component="a"
          href="/templates/expense-template-csv.csv"
          download
        >
          Download CSV
        </Button>
      </Box>
    </Box>
  );
}

export default ExpenseTemplatePanel;
