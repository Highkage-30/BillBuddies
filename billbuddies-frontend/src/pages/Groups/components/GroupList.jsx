import GroupItem from "./GroupItem";

function GroupList({ groups, onDelete, onOpen }) {
  return (
    <>
      {groups.map((group) => (
        <GroupItem
          key={group.groupId}
          group={group}
          onDelete={onDelete}
          onOpen={onOpen}
        />
      ))}
    </>
  );
}

export default GroupList;
