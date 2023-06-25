package io.github.planegg.allone.starter.service.tblautofill;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import io.github.planegg.allone.starter.common.util.PrjStringUtil;
import io.github.planegg.allone.starter.dto.CurrentUserCtx;
import io.github.planegg.allone.starter.entity.CommonColumn;
import io.github.planegg.allone.starter.service.user.CurrentUserContextHolder;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * mybatis plus 字段填充
 */
@Component
public class PrjMetaObjectHandler implements MetaObjectHandler {

    private final static Logger logger = LoggerFactory.getLogger(PrjMetaObjectHandler.class);
    /**
     * 存储通用字段的表字段名称与类属性名称的对应关系
     */
    private static Map<String,String> colNameFieldMap = new ConcurrentHashMap<>();
    private static Map<String,String> colNameDefaultMap = new ConcurrentHashMap<>();
    private final static String FIELD_CREATE_ID = "${allone.entity.com-col-name.id}";
    private final static String FIELD_CREATE_USER = "${allone.entity.com-col-name.create-user}";
    private final static String FIELD_CREATE_TIME = "${allone.entity.com-col-name.create-time}";
    private final static String FIELD_UPDATE_USER = "${allone.entity.com-col-name.update-user}";
    private final static String FIELD_UPDATE_TIME = "${allone.entity.com-col-name.update-time}";
    private final static String FIELD_TENANT_ID = "${allone.entity.com-col-name.tenant-id}";



    @Autowired
    private Environment environment;

    /**
     * 替换通用字段上的数据库字段名占位符
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    @PostConstruct
    private void init() throws IllegalAccessException, NoSuchFieldException {
        /**
         * 设置默认值
         */
        colNameDefaultMap.put(FIELD_CREATE_ID,"id");
        colNameDefaultMap.put(FIELD_CREATE_USER,"create_user");
        colNameDefaultMap.put(FIELD_CREATE_TIME,"create_time");
        colNameDefaultMap.put(FIELD_UPDATE_USER,"update_user");
        colNameDefaultMap.put(FIELD_UPDATE_TIME,"update_time");


        Field[] fields = CommonColumn.class.getDeclaredFields();
        for(Field aField : fields){
            TableId tableId = aField.getAnnotation(TableId.class);
            TableField tableField = aField.getAnnotation(TableField.class);
            if (tableId == null && tableField == null){
                continue;
            }
            InvocationHandler invocationHandler ;
            String originVal ;
            if (tableField != null){
                invocationHandler = Proxy.getInvocationHandler(tableField);
                originVal =tableField.value();
            }else {
                invocationHandler = Proxy.getInvocationHandler(tableId);
                originVal =tableId.value();
            }

            String colName = environment.resolvePlaceholders(originVal);
            //如果新值与旧值相同，表示并没有获取到配置
            if (PrjStringUtil.isEmpty(colName) || originVal.equals(colName)){
                colName = colNameDefaultMap.get(originVal);
                if (colName == null){
//                    throw new ItHandleException("通用字段中的【%s】字段没有配置，请配置后再启动！系统获取到的值是：%s",originVal,newVal);
                    continue;
                }
            }
            // 修改
//                 annotation注解的membervalues
            Field hField = invocationHandler.getClass().getDeclaredField("memberValues");
            // 因为这个字段是 private final 修饰，所以要打开权限
            hField.setAccessible(true);
            // 获取 memberValues
            Map<String, Object> memberValues = (Map) hField.get(invocationHandler);
            memberValues.put("value", colName);
            colNameFieldMap.put(originVal,aField.getName());
            logger.info("类{}中的注解占位符已成功替换，占位符：{}，替换值：{}",CommonColumn.class.getName(),originVal,colName);
            //保存关联关系，方便后续自动填充时，读取
        }
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        CurrentUserCtx currentUserCtx = getCurrentUserCtx();
        //TODO 灯笼：用户id改成long类型
        // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, colNameFieldMap.get(FIELD_CREATE_TIME), () -> LocalDateTime.now(), LocalDateTime.class);
        this.strictInsertFill(metaObject, colNameFieldMap.get(FIELD_CREATE_USER), () -> currentUserCtx.getUserId(), Long.class);
        this.strictUpdateFill(metaObject, colNameFieldMap.get(FIELD_UPDATE_TIME), () -> LocalDateTime.now(), LocalDateTime.class);
        this.strictInsertFill(metaObject, colNameFieldMap.get(FIELD_UPDATE_USER), () -> currentUserCtx.getUserId(), Long.class);
        if (colNameFieldMap.get(FIELD_TENANT_ID) != null){
            this.strictInsertFill(metaObject, colNameFieldMap.get(FIELD_TENANT_ID), () -> currentUserCtx.getTenantId(), Long.class);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        CurrentUserCtx currentUserCtx = getCurrentUserCtx();
        // 起始版本 3.3.3(推荐)
        this.strictUpdateFill(metaObject, colNameFieldMap.get(FIELD_UPDATE_TIME), () -> LocalDateTime.now(), LocalDateTime.class);
        this.strictInsertFill(metaObject, colNameFieldMap.get(FIELD_UPDATE_USER), () -> currentUserCtx.getUserId(), Long.class);

    }

    /**
     * 检查字段是否需要自动填充
     * @param insertFlag
     * @param fieldFill
     * @return
     */
    protected boolean chkFill(boolean insertFlag , FieldFill fieldFill){
        if (fieldFill == null ){
            return false;
        }

        if (insertFlag && FieldFill.INSERT.compareTo(fieldFill) == 0){
            return true;
        }
        if (FieldFill.INSERT_UPDATE.compareTo(fieldFill) == 0){
            return true;
        }
        return false;
    }

    /**
     * 非标准who字段自动填充
     * @param metaObject
     * @param insertFlag
     */
    protected void handleAutoFill(MetaObject metaObject,boolean insertFlag){
        Field[] fields = metaObject.getOriginalObject().getClass().getDeclaredFields();
        for (Field field : fields) {
            TableField $tableField = field.getAnnotation(TableField.class);
            if ($tableField == null ){
                continue;
            }
            if (insertFlag && $tableField.fill().compareTo(FieldFill.INSERT) != 0 ){
                continue;
            }
            if ($tableField.fill().compareTo(FieldFill.INSERT_UPDATE) != 0 ){
                continue;
            }
            if (LocalDateTime.class.isAssignableFrom(field.getType())){
                this.strictInsertFill(metaObject, $tableField.value() , () -> LocalDateTime.now(), LocalDateTime.class);
            }else {
                CurrentUserCtx currentUserCtx = getCurrentUserCtx();
                this.strictInsertFill(metaObject, $tableField.value() , () -> currentUserCtx.getUserId(), Long.class);
            }

        }
    }

    /**
     * 区分insert/update模式设置自动填充值
     * @param insertFlag
     * @param metaObject
     * @param fieldName
     * @param fieldVal
     * @param fieldType
     * @param <T>
     * @param <E>
     * @return
     */
    protected <T, E extends T> MetaObjectHandler setFieldFill(boolean insertFlag , MetaObject metaObject, String fieldName
            , Supplier<E> fieldVal, Class<T> fieldType){
        if (insertFlag){
            return this.strictInsertFill(metaObject, fieldName, fieldVal, fieldType);
        }
        return this.strictUpdateFill(metaObject, fieldName , fieldVal, fieldType);
    }

    /**
     * 获取当前用户上下文
     * @return
     */
    protected CurrentUserCtx getCurrentUserCtx(){
        CurrentUserCtx currentUserCtx = CurrentUserContextHolder.get();
        if (currentUserCtx == null){
            currentUserCtx = new CurrentUserCtx();
        }
        //TODO 灯笼：更改从数据库查出系统默认用户及租户
        if (currentUserCtx.getUserId() == null ){
            currentUserCtx.setUserId(1L);
        }
        if (currentUserCtx.getTenantId() == null ){
            currentUserCtx.setTenantId(1L);
        }
        return currentUserCtx;
    }



}
