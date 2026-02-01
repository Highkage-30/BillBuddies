import { Box, Button } from "@mui/material";
import { useState } from "react";
import MemberMultiSelect from "./MemberMultiSelect";

function AddMembersPanel({ options, onSubmit }) {
  const [selected, setSelected] = useState([]);

  const handleAdd = () => {
    if (selected.length === 0) return;

    // ✅ selected is already valid
    onSubmit(selected);
    setSelected([]);
  };

  return (
    <Box className="add-members-panel">
      <h3>Add Members</h3>

      <MemberMultiSelect
        options={options}
        selected={selected}
        onChange={setSelected}
      />

      <div className="add-members-footer">
        <Button
          variant="contained"
          onClick={handleAdd}
        >
          Add Selected Members
        </Button>
      </div>
    </Box>
  );
}

export default AddMembersPanel;
