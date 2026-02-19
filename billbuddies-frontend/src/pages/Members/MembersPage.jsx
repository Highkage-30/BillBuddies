import { useEffect, useState } from "react";
import { Container, Typography, TextField, Box } from "@mui/material";
import "./MembersPage.css";
import MemberList from "./components/MemberList";
import { fetchMembers } from "../../api/memberApi";

function MembersPage() {
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [hasError, setHasError] = useState(false);
  const [search, setSearch] = useState("");

  useEffect(() => {
    loadMembers();
  }, []);

  const loadMembers = async () => {
    try {
      setLoading(true);
      const data = await fetchMembers();
      setMembers(data);
      setHasError(false);
    } catch {
      setHasError(true);
    } finally {
      setLoading(false);
    }
  };

  const filteredMembers = members.filter((member) =>
    member.memberName.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) return <p className="members-status">Loading members...</p>;
  if (hasError)
    return (
      <p className="members-error">
        Unable to load members. Please try again later.
      </p>
    );

  return (
    <Container className="members-page">
      <div className="members-panel">
        {/* Header */}
        <Box className="members-header">
          <Typography variant="h5">Members</Typography>

          <TextField
            size="small"
            placeholder="Search members"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </Box>

        {/* LIST OR EMPTY STATE */}
        {filteredMembers.length === 0 && search ? (
          <p className="members-empty">
            No members found matching <strong>"{search}"</strong>
          </p>
        ) : (
          <MemberList members={filteredMembers} />
        )}
      </div>
    </Container>
  );
}

export default MembersPage;
