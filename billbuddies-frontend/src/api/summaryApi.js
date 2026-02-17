import axiosInstance from "./axiosInstance";

export const fetchGroupSummary = async (groupId) => {
  const res = await axiosInstance.get(
    `/groups/${groupId}/statement`
  );
  return res.data;
};


export const downloadGroupSummary = async (groupId) => {
  const res = await axiosInstance.get(
    `/groups/${groupId}/summary/download`,
    {
      responseType: "blob", // 🔥 IMPORTANT
    }
  );
  return res.data;
};