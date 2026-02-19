import { NavLink, useParams } from "react-router-dom";
import "./GroupLayout.css";

function GroupSidebar() {
  const { groupId } = useParams();

  return (
    <div className="group-sidebar">
      <NavLink to={`/groups/${groupId}/expense`}end>
        Expenses
      </NavLink>
      <NavLink to={`/groups/${groupId}/members`}>
        Members
      </NavLink>
      <NavLink to={`/groups/${groupId}/pool`}>
        Pool
      </NavLink>
      <NavLink to={`/groups/${groupId}/settlement`}>
        Settlement
      </NavLink>
      <NavLink to={`/groups/${groupId}/summary`}>
        Summary
      </NavLink>
    </div>
  );
}

export default GroupSidebar;
