package com.hrm.Service;

import java.util.List;
import com.hrm.DAO.AccountManagerDAO;
import com.hrm.DTO.AccountManagerDTO;

public class AccountManagerService {
    private AccountManagerDAO accountDAO;

    public AccountManagerService() {
        this.accountDAO = new AccountManagerDAO();
    }

    public List<AccountManagerDTO> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }

    public AccountManagerDTO getAccountByMaNV(String maNV) {
        return accountDAO.getAccountByMaNV(maNV);
    }

    public List<AccountManagerDTO> getAccountsByRoleId(String roleId) {
        return accountDAO.getAccountsByRoleId(roleId);
    }

    public boolean addAccount(AccountManagerDTO account) {
        return accountDAO.addAccount(account);
    }

    public boolean updateAccount(AccountManagerDTO account) {
        return accountDAO.updateAccount(account);
    }

    public boolean deleteAccount(String maNV) {
        return accountDAO.deleteAccount(maNV);
    }

    public boolean changePassword(String maNV, String newPassword) {
        return accountDAO.changePassword(maNV, newPassword);
    }

    public boolean changePasswordByManv(String manv, String newPassword) {
        return accountDAO.changePasswordByManv(manv, newPassword);
    }

    public boolean setAccountStatus(String maNV, int status) {
        return accountDAO.setAccountStatus(maNV, status);
    }

    public List<String> createAccountsForEmployeesWithoutAccount() {
        return accountDAO.createAccountsForEmployeesWithoutAccount();
    }
}
