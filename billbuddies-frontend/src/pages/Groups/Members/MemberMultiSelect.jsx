import {
  Autocomplete,
  TextField,
  Chip,
} from "@mui/material";
import { createFilterOptions } from "@mui/material/Autocomplete";

const filter = createFilterOptions();

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
      filterOptions={(opts, params) => {
        const filtered = filter(opts, params);
        const { inputValue } = params;

        // Check if input already exists
        const isExisting = opts.some(
          (option) =>
            option.toLowerCase() ===
            inputValue.toLowerCase()
        );

        // Add "Add <input>" option
        if (inputValue !== "" && !isExisting) {
          filtered.push(`Add "${inputValue}"`);
        }

        return filtered;
      }}
      onChange={(e, newValue) => {
        const cleaned = newValue.map((v) => {
          if (v.startsWith('Add "')) {
            return v.replace('Add "', "").replace('"', "");
          }
          return v;
        });

        onChange(cleaned);
      }}
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
