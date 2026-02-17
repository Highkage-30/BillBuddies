import axiosInstance from "./axiosInstance";

// GET settlement rows
export const fetchGroupSettlement = async (groupId) => {
  const res = await axiosInstance.get(
    `/groups/${groupId}/settle`
  );
  return res.data;
};

// POST row-level settlement
export const executeSettlementRow = async (groupId, payload) => {
  return axiosInstance.post(
    `/groups/${groupId}/settle/execute`,
    payload
  );
};

// POST manual/global settlement
export const executeSettlement = async (groupId) => {
  return axiosInstance.post(
    `/groups/${groupId}/settle`
  );
};
export const downloadSettlementReport = async (groupId) => {
  const response = await axiosInstance.get(
    `/groups/${groupId}/settlement/report`,
    { responseType: "blob" } // 🔥 IMPORTANT
  );

  return response;
};
