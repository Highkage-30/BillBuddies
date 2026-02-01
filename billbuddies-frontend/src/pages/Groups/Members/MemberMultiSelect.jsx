import {
  Autocomplete,
  TextField,
  Chip,
} from "@mui/material";

function MemberMultiSelect({
  options,
  selected,
  onChange,
}) {
  return (
    <Autocomplete
      multiple
      freeSolo
      options={options}
      value={selected}
      onChange={(e, newValue) => onChange(newValue)}
      renderTags={(value, getTagProps) =>
        value.map((option, index) => (
          <Chip
            variant="outlined"
            label={option}
            {...getTagProps({ index })}
          />
        ))
      }
      renderInput={(params) => (
        <TextField
          {...params}
          label="Search or add members"
          placeholder="Type name and press Enter"
        />
      )}
    />
  );
}

export default MemberMultiSelect;
