package com.walker.service.impl;

import com.walker.config.Config;
import com.walker.mode.Page;
import com.walker.dao.RoleUserRepository;
import com.walker.mode.RoleUser;
import com.walker.service.RoleUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("roleUserService")
public class RoleUserServiceImpl implements RoleUserService {

    @Autowired
    private RoleUserRepository roleUserRepository;


    @Override
    public List<RoleUser> saveAll(List<RoleUser> objs) {
        return roleUserRepository.saveAll(objs);
    }

    @Override
    public Integer delete(RoleUser obj) {
        roleUserRepository.deleteById(obj.getID());
        return 1;
    }

    @Override
    public RoleUser get(RoleUser obj) {
        Optional<RoleUser> result = roleUserRepository.findById(obj.getID());
        return result.isPresent() ? result.get() : null;
    }

    @Override
    public List<RoleUser> finds(RoleUser obj, Page page) {
        Pageable pageable = Config.turnTo(page);
        org.springframework.data.domain.Page<RoleUser> res = roleUserRepository.findAll(this.getSpecification(obj), pageable);
        page.setNum(res.getTotalElements());
        return res.getContent();
    }

    @Override
    public Integer count(RoleUser obj) {
        long res = roleUserRepository.count(this.getSpecification(obj));
        return new Long(res).intValue();
    }


    private Specification getSpecification(RoleUser obj) {
        return new Specification<RoleUser>() {
            @Override
            public Predicate toPredicate(Root<RoleUser> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotEmpty(obj.getID())) {
                    list.add(criteriaBuilder.like(root.get("ID"), "%" + obj.getID() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getS_MTIME())) {
                    list.add(criteriaBuilder.greaterThan(root.get("S_MTIME"), obj.getS_MTIME()));
                }
                if (StringUtils.isNotEmpty(obj.getS_ATIME())) {
                    list.add(criteriaBuilder.greaterThan(root.get("S_ATIME"), obj.getS_ATIME()));
                }
                if (StringUtils.isNotEmpty(obj.getS_FLAG())) {
                    list.add(criteriaBuilder.equal(root.get("S_FLAG"), obj.getS_FLAG()));
                }


                if (StringUtils.isNotEmpty(obj.getROLE_ID())) {
                    list.add(criteriaBuilder.equal(root.get("ROLE_ID"), obj.getROLE_ID()));
                }
                if (StringUtils.isNotEmpty(obj.getUSER_ID())) {
                    list.add(criteriaBuilder.equal(root.get("USER_ID"), obj.getUSER_ID()));
                }

                return criteriaBuilder.and(list.toArray(new Predicate[0]));
            }
        };
    }

    @Override
    public Integer[] deleteAll(List<String> ids) {
        int res = roleUserRepository.selfDeleteAll(ids);
        return new Integer[]{res};
    }


}





