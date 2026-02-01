import {
  Typography,
  TextField,
  Box,
  Button,
  Autocomplete,
  Chip,
} from "@mui/material";

function AddGroupForm({
  groupName,
  groupDescription,
  members,
  selectedMembers,
  errors,
  loading,
  onGroupNameChange,
  onGroupDescriptionChange,
  onMembersChange,
  onSubmit,
}) {
  return (
    <form className="add-group-panel" onSubmit={onSubmit}>
      <div className="add-group-header">
        <Typography variant="h5">Add Group</Typography>
      </div>

      <div className="add-group-row">
        <label>Group Name</label>
        <TextField
          size="small"
          fullWidth
          value={groupName}
          onChange={onGroupNameChange}
          error={!!errors.groupName}
          helperText={errors.groupName}
        />
      </div>

      <div className="add-group-row">
        <label>Group Description</label>
        <TextField
          size="small"
          fullWidth
          multiline
          rows={3}
          value={groupDescription}
          onChange={onGroupDescriptionChange}
        />
      </div>

      <div className="add-group-row">
        <label>Add Members</label>
        <Box>
          <Autocomplete
            multiple
            freeSolo
            options={members}
            value={selectedMembers}
            filterOptions={(options, params) => {
              const filtered = options.filter((o) =>
                o.toLowerCase().includes(params.inputValue.toLowerCase())
              );
              const input = params.inputValue.trim();
              if (input && !options.includes(input)) {
                filtered.push(`Add "${input}"`);
              }
              return filtered;
            }}
            onChange={(e, newValue) => onMembersChange(newValue)}
            renderTags={(value, getTagProps) =>
              value.map((option, index) => (
                <Chip label={option} {...getTagProps({ index })} />
              ))
            }
            renderInput={(params) => (
              <TextField
                {...params}
                size="small"
                error={!!errors.members}
                placeholder="Search or add members"
              />
            )}
          />
          {errors.members && (
            <Typography className="field-error">
              {errors.members}
            </Typography>
          )}
        </Box>
      </div>

      <div className="add-group-footer">
        <Button type="submit" variant="contained" disabled={loading}>
          {loading ? "Creating..." : "Add Group"}
        </Button>
      </div>
    </form>
  );
}

export default AddGroupForm;
