package com.cuckoo.framework.navi.engine.datasource.service;

import com.cuckoo.framework.navi.common.NaviError;
import com.cuckoo.framework.navi.common.exception.NaviSystemException;
import com.cuckoo.framework.navi.engine.core.INaviDB;
import com.cuckoo.framework.navi.engine.core.INaviDataSource;

public abstract class AbstractNaviDbService implements INaviDB {

    protected INaviDataSource dataSource;

    public void setDataSource(INaviDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public INaviDataSource getDataSource() {
        return dataSource;
    }

    public void afterPropertiesSet() throws Exception {
        if (dataSource == null) {
            throw new NaviSystemException("the dataSource is null!",
                NaviError.SYSERROR.code());
        }
    }

}
