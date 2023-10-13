package com.walker.spring.service.impl;

import com.walker.core.mode.Page;
import com.walker.core.mode.sys.Action;
import com.walker.service.ActionService;
import com.walker.spring.dao.ActionRepository;
import com.walker.spring.util.SpringUtil;
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

@Service("actionService")
public class ActionServiceImpl implements ActionService {


    @Autowired
    private ActionRepository actionRepository;

    @Override
    public List<Action> saveAll(List<Action> objs) {
        return actionRepository.saveAll(objs);
    }

    @Override
    public Integer delete(Action obj) {
        actionRepository.deleteById(obj.getID());
        return 1;
    }

    @Override
    public Action get(Action obj) {
        Optional<Action> result = actionRepository.findById(obj.getID());
        return result.isPresent() ? result.get() : null;
    }

    @Override
    public List<Action> finds(Action obj, Page page) {
        Pageable pageable = SpringUtil.turnTo(page);
        org.springframework.data.domain.Page<Action> res = actionRepository.findAll(this.getSpecification(obj), pageable);
        page.setTotal(res.getTotalElements());
        return res.getContent();
    }

    @Override
    public Integer count(Action obj) {
        long res = actionRepository.count(this.getSpecification(obj));
        return new Long(res).intValue();
    }


    private Specification getSpecification(Action obj) {
        return new Specification<Action>() {
            @Override
            public Predicate toPredicate(Root<Action> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
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
                if (StringUtils.isNotEmpty(obj.getTYPE())) {
                    list.add(criteriaBuilder.like(root.get("TYPE"), "%" + obj.getTYPE() + "%"));
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
        int res = actionRepository.selfDeleteAll(new HashSet<>(ids));

        return new Integer[]{res};
    }
}
