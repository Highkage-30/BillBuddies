import axiosInstance from "./axiosInstance";

export const fetchGroupMembers = async (groupId) => {
  const res = await axiosInstance.get(
    `/groups/${groupId}/members`
  );
  return res.data;
};

export const fetchGroupStatement = async (groupId) => {
  const res = await axiosInstance.get(
    `/groups/${groupId}/statement`
  );
  return res.data;
};

export const addMembersToGroupBulk = async (
  groupId,
  memberNames
) => {
  return axiosInstance.post(
    `/groups/${groupId}/members`,
    { memberNames }
  );
};
export const uploadGroupMembers = (groupId, file) => {
  const formData = new FormData();
  formData.append("file", file);

  return axiosInstance.post(
    `/groups/${groupId}/members/upload`,
    formData,
    {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    }
  );
};
