import { Box, Button, Typography, Divider } from "@mui/material";
import DownloadIcon from "@mui/icons-material/Download";
import PeopleIcon from "@mui/icons-material/People";

function MemberTemplatePanel() {
  return (
    <Box p={2}>
      {/* HEADER */}
      <Box display="flex" alignItems="center" gap={1} mb={1}>
        
      <Typography
        variant="subtitle1"
        fontWeight={600}
        gutterBottom
      >
        Member Upload Template Files -
      </Typography>
      </Box>

      <Divider sx={{ mb: 2 }} />

      {/* TEMPLATE FILES */}
      <Box display="flex" flexDirection="column" gap={1}>
        <Button
          variant="outlined"
          startIcon={<DownloadIcon />}
          component="a"
          href="/templates/member-template-excel.xlsx"
          download
        >
          Download Excel
        </Button>

        <Button
          variant="outlined"
          startIcon={<DownloadIcon />}
          component="a"
          href="/templates/member-template-csv.csv"
          download
        >
          Download CSV
        </Button>
      </Box>
    </Box>
  );
}

export default MemberTemplatePanel;
