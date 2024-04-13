package com.greendao.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.actor.testapplication.bean.BirthItem;
import com.actor.testapplication.bean.NovelBean;

import com.greendao.gen.BirthItemDao;
import com.greendao.gen.NovelBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig birthItemDaoConfig;
    private final DaoConfig novelBeanDaoConfig;

    private final BirthItemDao birthItemDao;
    private final NovelBeanDao novelBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        birthItemDaoConfig = daoConfigMap.get(BirthItemDao.class).clone();
        birthItemDaoConfig.initIdentityScope(type);

        novelBeanDaoConfig = daoConfigMap.get(NovelBeanDao.class).clone();
        novelBeanDaoConfig.initIdentityScope(type);

        birthItemDao = new BirthItemDao(birthItemDaoConfig, this);
        novelBeanDao = new NovelBeanDao(novelBeanDaoConfig, this);

        registerDao(BirthItem.class, birthItemDao);
        registerDao(NovelBean.class, novelBeanDao);
    }
    
    public void clear() {
        birthItemDaoConfig.clearIdentityScope();
        novelBeanDaoConfig.clearIdentityScope();
    }

    public BirthItemDao getBirthItemDao() {
        return birthItemDao;
    }

    public NovelBeanDao getNovelBeanDao() {
        return novelBeanDao;
    }

}
