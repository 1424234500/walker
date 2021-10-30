package com.walker.service.impl;

import com.walker.config.Config;
import com.walker.core.mode.Page;
import com.walker.dao.RoleRepository;
import com.walker.dao.RoleUserRepository;
import com.walker.mode.Role;
import com.walker.mode.RoleUser;
import com.walker.service.RoleService;
import com.walker.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service("roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleUserRepository roleUserRepository;


    @Override
    public List<Role> saveAll(List<Role> objs) {
        return roleRepository.saveAll(objs);
    }

    @Override
    public Integer delete(Role obj) {
        roleRepository.deleteById(obj.getID());
        roleUserRepository.deleteAllByRoleId(Arrays.asList(obj.getID()));
        return 1;
    }

    @Override
    public Role get(Role obj) {
        Optional<Role> result = roleRepository.findById(obj.getID());
        return result.isPresent() ? result.get() : null;
    }

    @Override
    public List<Role> finds(Role obj, Page page) {
        Pageable pageable = Config.turnTo(page);
        org.springframework.data.domain.Page<Role> res = roleRepository.findAll(this.getSpecification(obj), pageable);
        page.setNum(res.getTotalElements());
        return res.getContent();
    }

    @Override
    public List<RoleUser> finds(RoleUser obj, Page page) {
        Pageable pageable = Config.turnTo(page);
        org.springframework.data.domain.Page<RoleUser> res = roleUserRepository.findAll(this.getSpecification(obj), pageable);
        page.setNum(res.getTotalElements());
        return res.getContent();
    }

    @Override
    public Integer count(Role obj) {
        long res = roleRepository.count(this.getSpecification(obj));
        return new Long(res).intValue();
    }

    @Override
    public Integer count(RoleUser obj) {
        long res = roleUserRepository.count(this.getSpecification(obj));
        return new Long(res).intValue();
    }


    private Specification getSpecification(Role obj) {
        return new Specification<Role>() {
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

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


                if (StringUtils.isNotEmpty(obj.getNAME())) {
                    list.add(criteriaBuilder.like(root.get("NAME"), "%" + obj.getNAME() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getNUM())) {
                    list.add(criteriaBuilder.greaterThan(root.get("NUM"), obj.getNUM()));
                }
                if (StringUtils.isNotEmpty(obj.getLEVEL())) {
                    list.add(criteriaBuilder.equal(root.get("LEVEL"), obj.getLEVEL()));
                }

                return criteriaBuilder.and(list.toArray(new Predicate[0]));
            }
        };
    }


    private Specification getSpecification(RoleUser obj) {
        return new Specification<Role>() {
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

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
        int res = roleRepository.selfDeleteAll(ids);
        roleUserRepository.deleteAllByRoleId(ids);
        return new Integer[]{res};
    }

    /**
     * 获取关联表role role_user的赋予用户的权限
     *
     * @param id
     * @param sFlag 1已有的 0没有的  ''则返回所有
     */
    @Override
    public List<Role> getRoles(String id, String sFlag) {
//        return roleRepository.getRoles(id, sFlag);
//        "select * from ( " +
//        "select r.ID,r.LEVEL,r.NAME,r.NUM,r.s_ATIME,r.s_MTIME,ifnull(ru.S_FLAG,'0') S_FLAG " +
//        "from W_ROLE r " +
//        "left join W_ROLE_USER ru " +
//        "on ru.USER_ID=? and r.ID=ru.ROLE_ID " +
//        " ) t " +
//        "where S_FLAG like concat('%', ?, '%') ", id, sFlag);
        List<RoleUser> roleUserList = roleUserRepository.findByUserId(id);  //部门/用户的配置关联列表
        Map<String, RoleUser> roleUserMap = new HashMap<>();
        for (RoleUser roleUser : roleUserList) {
            roleUserMap.put(roleUser.getROLE_ID(), roleUser);
        }
//        List<Role> roleList = roleRepository.findByIds(roleIdList);
        List<Role> roleList = roleRepository.findAll(); //角色列表
        List<Role> roleListRes = new ArrayList<>();

        //有关联角色且为sflag=1 则有 否则无
        for (Role role : roleList) {
            RoleUser roleUser = roleUserMap.get(role.getID());
            if (roleUser == null) {
                role.setS_FLAG(Config.FALSE);
            } else {
                role.setS_FLAG(roleUser.getS_FLAG());
                role.setS_MTIME(roleUser.getS_MTIME());
                role.setS_ATIME(roleUser.getS_ATIME());
            }
            if (role.getS_FLAG().contains(sFlag) || sFlag.length() == 0) {
                roleListRes.add(role);
            }
        }
        return roleListRes;
    }

    /**
     * 保存角色关联
     *
     * @param id         用户/部门
     * @param onRoleIds  开启的角色id列表
     * @param offRoleIds 关闭的角色id列表
     */
    @Override
    public List<RoleUser> saveRoles(String id, List<String> onRoleIds, List<String> offRoleIds) {
        List<RoleUser> roleUserList = new ArrayList<>();
        for (String roleId : onRoleIds) {
            if (roleId.length() > 0) {
                RoleUser roleUser = new RoleUser();
                roleUser.setROLE_ID(roleId);
                roleUser.setUSER_ID(id);
                roleUser.setID(roleId + ":" + id);
                roleUser.setS_MTIME(TimeUtil.getTimeYmdHms());
                roleUser.setS_ATIME(TimeUtil.getTimeYmdHms());
                roleUser.setS_FLAG(Config.TRUE);
                roleUserList.add(roleUser);
            }
        }
        for (String roleId : offRoleIds) {
            if (roleId.length() > 0) {
                RoleUser roleUser = new RoleUser();
                roleUser.setROLE_ID(roleId);
                roleUser.setUSER_ID(id);
                roleUser.setID(roleId + ":" + id);
                roleUser.setS_MTIME(TimeUtil.getTimeYmdHms());
                roleUser.setS_ATIME(TimeUtil.getTimeYmdHms());
                roleUser.setS_FLAG(Config.FALSE);
                roleUserList.add(roleUser);
            }
        }
        return roleUserRepository.saveAll(roleUserList);
    }


}





