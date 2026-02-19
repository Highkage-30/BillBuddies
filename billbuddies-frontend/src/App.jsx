import { BrowserRouter, Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar/Navbar";
import HomePage from "./pages/Home/HomePage";
import MembersPage from "./pages/Members/MembersPage";
import GroupsPage from "./pages/Groups/GroupsPage";
import AddGroupPage from "./pages/Groups/AddGroup/AddGroupPage";
import GroupLayout from "./pages/Groups/GroupLayout/GroupLayout";
import ExpensesPage from "./pages/Groups/Expenses/ExpensesPage";
import AddExpensePage from "./pages/Groups/Expenses/NewExpense/AddExpensePage";
import GroupSummaryPage from "./pages/Groups/Summary/GroupSummaryPage";
import GroupSettlementPage from "./pages/Groups/Settlement/GroupSettlementPage";
import GroupMembersPage from "./pages/Groups/Members/GroupMembersPage";
import MemberDetailPage from "./pages/Members/MemberDetail/MemberDetailPage";
import PoolPage from "./pages/Groups/Pool/PoolPage";
function App() {
  return (
    <BrowserRouter>
      <Navbar />
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/members" element={<MembersPage />} />
          <Route path="/members/:memberId" element={<MemberDetailPage/>} />
          <Route path="/groups" element={<GroupsPage />} />
          <Route path="/groups/new" element={<AddGroupPage />} />
          <Route path="/groups/:groupId" element={<GroupLayout />}>
            {/* default */}
            <Route index element={<ExpensesPage />} />

            {/* expenses */}
            <Route path="expense" element={<ExpensesPage />} />
            {/* <Route path="expense/:expenseId" element={<ExpenseDetailsPage />}/>  */}
            <Route path="expense/new" element={<AddExpensePage />} />
            <Route path="summary" element={<GroupSummaryPage />}/>
            <Route path="settlement" element={<GroupSettlementPage />}/>
            <Route path="members" element={<GroupMembersPage />}/>
            <Route path="pool" element={<PoolPage />} />
          </Route>
        </Routes>
    </BrowserRouter>
  );
}

export default App;
