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

    public AccountManagerDTO getAccountByUserId(String userId) {
        return accountDAO.getAccountByUserId(userId);
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

    public boolean deleteAccount(String userId) {
        return accountDAO.deleteAccount(userId);
    }

    public boolean changePassword(String userId, String newPassword) {
        return accountDAO.changePassword(userId, newPassword);
    }

    public boolean setAccountStatus(String userId, int status) {
        return accountDAO.setAccountStatus(userId, status);
    }
}
