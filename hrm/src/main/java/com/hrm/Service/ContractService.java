package com.hrm.Service;

import java.util.List;
import com.hrm.DAO.ContractDAO;
import com.hrm.DTO.ContractDTO;

public class ContractService {
    private ContractDAO contractDAO;

    public ContractService() {
        this.contractDAO = new ContractDAO();
    }

    public List<ContractDTO> getAllContracts() {
        return contractDAO.getAllContracts();
    }

    public ContractDTO getContractByMa(String maHopDong) {
        return contractDAO.getContractByMa(maHopDong);
    }

    public List<ContractDTO> getContractsByMaNV(String maNV) {
        return contractDAO.getContractsByMaNV(maNV);
    }

    public boolean addContract(ContractDTO contract) {
        return contractDAO.addContract(contract);
    }

    public boolean updateContract(ContractDTO contract) {
        return contractDAO.updateContract(contract);
    }

    public boolean deleteContract(String maHopDong) {
        return contractDAO.deleteContract(maHopDong);
    }
}
