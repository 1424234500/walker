package com.walker.service.impl;

import com.walker.config.Config;
import com.walker.core.mode.Page;
import com.walker.core.mode.sys.SysConfig;
import com.walker.dao.SysConfigRepository;
import com.walker.service.SysConfigService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service("sysConfigService")
public class SysConfigServiceImpl implements SysConfigService {


    @Autowired
    private SysConfigRepository configRepository;

    @Override
    public List<SysConfig> saveAll(List<SysConfig> objs) {
        return configRepository.saveAll(objs);
    }

    @Override
    public Integer delete(SysConfig obj) {
        configRepository.deleteById(obj.getID());
        return 1;
    }

    @Override
    public SysConfig get(SysConfig obj) {
        Optional<SysConfig> result = configRepository.findById(obj.getID());
        return result.isPresent() ? result.get() : null;
    }

    @Override
    public List<SysConfig> finds(SysConfig obj, Page page) {
        Pageable pageable = Config.turnTo(page);
        org.springframework.data.domain.Page<SysConfig> res = configRepository.findAll(this.getSpecification(obj), pageable);
        page.setTotal(res.getTotalElements());
        return res.getContent();
    }

    @Override
    public Integer count(SysConfig obj) {
        long res = configRepository.count(this.getSpecification(obj));
        return new Long(res).intValue();
    }


    private Specification getSpecification(SysConfig obj) {
        return new Specification<SysConfig>() {
            @Override
            public Predicate toPredicate(Root<SysConfig> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
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
                if (StringUtils.isNotEmpty(obj.getABOUT())) {
                    list.add(criteriaBuilder.like(root.get("ABOUT"), "%" + obj.getABOUT() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getVALUE())) {
                    list.add(criteriaBuilder.like(root.get("VALUE"), "%" + obj.getVALUE() + "%"));
                }
                return criteriaBuilder.and(list.toArray(new Predicate[0]));
            }
        };
    }

    @Override
    public Integer[] deleteAll(List<String> ids) {
        int res = configRepository.selfDeleteAll(new HashSet<>(ids));

        return new Integer[]{res};
    }
}
