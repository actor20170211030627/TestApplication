package com.greendao.gen;

import com.actor.testapplication.bean.BirthItem;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.Map;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig birthItemDaoConfig;

    private final BirthItemDao birthItemDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        birthItemDaoConfig = daoConfigMap.get(BirthItemDao.class).clone();
        birthItemDaoConfig.initIdentityScope(type);

        birthItemDao = new BirthItemDao(birthItemDaoConfig, this);

        registerDao(BirthItem.class, birthItemDao);
    }
    
    public void clear() {
        birthItemDaoConfig.clearIdentityScope();
    }

    public BirthItemDao getBirthItemDao() {
        return birthItemDao;
    }

}
