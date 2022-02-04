package com.walker.service.impl;

import com.walker.config.Config;
import com.walker.mode.Page;
import com.walker.dao.DeptRepository;
import com.walker.dao.RoleUserRepository;
import com.walker.mode.school.Dept;
import com.walker.service.DeptService;
import com.walker.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service("deptService")
public class DeptServiceImpl implements DeptService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private DeptRepository deptRepository;
    @Autowired
    private RoleUserRepository roleUserRepository;


    /**
     * 构建机构树
     *
     * @param objs
     */
    @Override
    public List<Dept> saveAll(List<Dept> objs) {
        Set<String> pids = new LinkedHashSet<>();
        for (Dept obj : objs) {
            if (obj.getP_ID() != null)   //可能没有上级
                pids.add(obj.getP_ID());
        }
        Map<String, Dept> index = new LinkedHashMap<>();

        //1.上级在表中
        List<Dept> pobjs = pids.size() > 0 ? deptRepository.findAllByID(pids) : new ArrayList<>();
        for (Dept obj : pobjs) {
            index.put(obj.getID(), obj);
        }
        //2.上级在list中
        for (Dept obj : objs) {
            index.put(obj.getID(), obj);
        }


        List<Dept> oks = new ArrayList<>();
        for (Dept obj : objs) {
            if (obj.getP_ID() != null && obj.getP_ID().length() > 0 && !obj.getP_ID().equals(obj.getID())) {
                Dept pobj = index.get(obj.getP_ID());
                if (pobj == null) {//表中或list中 上级不存在
                    log.error("try save dept not exists pid " + obj);
                } else {//上级存在 复用机构树
                    if (obj.getPATH() == null || obj.getPATH().length() == 0) {
                        obj.setPATH(pobj.getPATH() + "/" + obj.getID());
                        obj.setPATH_NAME(pobj.getPATH_NAME() + "/" + obj.getNAME());
                    }
                    oks.add(obj);
                }
            } else {//无上级 root
                obj.setPATH("/" + obj.getID());
                obj.setPATH_NAME("/" + obj.getNAME());
                oks.add(obj);
            }
        }
        for (Dept obj : oks) {
            if (StringUtils.isEmpty(obj.getS_MTIME())) {
                obj.setS_MTIME(TimeUtil.getTimeYmdHms());
            }
//            if(obj.getLEVEL() < 0){
//                obj.setLEVEL(obj.getPATH().split("/").length + "");
//            }
        }

        return deptRepository.saveAll(oks);
    }

    @Override
    public Integer delete(Dept obj) {
        deleteAll(Arrays.asList(obj.getID()));
        return 1;
    }

    @Override
    public Dept get(Dept obj) {
        Optional<Dept> result = deptRepository.findById(obj.getID());
        return result.isPresent() ? result.get() : null;
    }

    @Override
    public List<Dept> finds(Dept obj, Page page) {
        Pageable pageable = Config.turnTo(page);
        org.springframework.data.domain.Page<Dept> res = deptRepository.findAll(this.getSpecification(obj), pageable);
        page.setNum(res.getTotalElements());
        return res.getContent();
    }

    @Override
    public Integer count(Dept obj) {
        long res = deptRepository.count(this.getSpecification(obj));
        return new Long(res).intValue();
    }

    @Override
    public List<Dept> findsRoot(Page page) {
        Pageable pageable = Config.turnTo(page);
        org.springframework.data.domain.Page<Dept> res = deptRepository.findsRoot(pageable);
        page.setNum(res.getTotalElements());
        return res.getContent();
    }

    private Specification getSpecification(Dept obj) {
        return new Specification<Dept>() {
            @Override
            public Predicate toPredicate(Root<Dept> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotEmpty(obj.getID())) {
                    list.add(criteriaBuilder.like(root.get("ID"), "%" + obj.getID() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getS_MTIME())) {
                    list.add(criteriaBuilder.greaterThan(root.get("S_MTIME"), obj.getS_MTIME()));
                }
                if (StringUtils.isNotEmpty(obj.getS_FLAG())) {
                    list.add(criteriaBuilder.equal(root.get("S_FLAG"), obj.getS_FLAG()));
                }


                if (StringUtils.isNotEmpty(obj.getNAME())) {
                    list.add(criteriaBuilder.like(root.get("NAME"), "%" + obj.getNAME() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getP_ID())) {
                    list.add(criteriaBuilder.equal(root.get("P_ID"), obj.getP_ID()));
                }
                if (StringUtils.isNotEmpty(obj.getPATH())) {
                    list.add(criteriaBuilder.like(root.get("PATH"), obj.getPATH() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getPATH_NAME())) {
                    list.add(criteriaBuilder.like(root.get("PATH_NAME"), "%" + obj.getPATH_NAME() + "%"));
                }
                return criteriaBuilder.and(list.toArray(new Predicate[0]));
            }
        };
    }

    @Override
    public Integer[] deleteAll(List<String> ids) {
        Integer[] res = new Integer[ids.size()];
        int i = 0;
        deptRepository.selfDeleteAll(new HashSet<>(ids));
//        for(String deptId : ids) {
//            List<Dept> depts = deptRepository.findAllByPATH(deptId);
//            Set<String> deptIds = new LinkedHashSet<>();
//            deptIds.add(deptId);
//            for(Dept obj : depts){
//                deptIds.add(obj.getID());
//            }
//            int cc = 0;
//            if(deptIds.size() > 0) {
//                cc = deptRepository.selfDeleteAll(deptIds);
//            }
//            res[i++] = cc;
//        }
        return res;
    }
}
