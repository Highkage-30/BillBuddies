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

export const addMemberToGroup = async (groupId, payload) => {
  return axiosInstance.post(
    `/groups/${groupId}/members`,
    payload
  );
};
