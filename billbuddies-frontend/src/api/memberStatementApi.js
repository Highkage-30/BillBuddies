import axiosInstance from "./axiosInstance";

export const fetchMemberStatement = async (memberId) => {
  const res = await axiosInstance.get(
    `/members/${memberId}/statement`
  );
  return res.data;
};
