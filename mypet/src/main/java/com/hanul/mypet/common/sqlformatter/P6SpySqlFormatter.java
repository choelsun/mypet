package com.hanul.mypet.common.sqlformatter;

import java.util.Locale;

import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.context.annotation.Configuration;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

import jakarta.annotation.PostConstruct;

// 설정파일을 만들기 위한 애노테이션 or Bean을 등록하기 위한 애노테이션
@Configuration
public class P6SpySqlFormatter implements MessageFormattingStrategy {
	
	// 의존성 주입이 이루어진 후 초기화를 수행하는 메서드
	// 이 메서드는 다른 리소스에서 호출되지 않는다해도 수행
	@PostConstruct
	public void setLogMessageFormat() {
		P6SpyOptions.getActiveInstance().setLogMessageFormat(this.getClass().getName());
	}
	
	@Override
	public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared,
			String sql, String url) {
		sql = formatSql(category, sql);
		
		return String.format("[%s] | %d ms | %s", category, elapsed, formatSql(category, sql));
	}
	
    private String formatSql(String category, String sql) {
        if (sql != null && !sql.trim().isEmpty() && Category.STATEMENT.getName().equals(category)) {
            String trimmedSQL = sql.trim().toLowerCase(Locale.ROOT);
            if (trimmedSQL.startsWith("create") || trimmedSQL.startsWith("alter") || trimmedSQL.startsWith("comment")) {
                sql = FormatStyle.DDL.getFormatter().format(sql);
            } else {
                sql = FormatStyle.BASIC.getFormatter().format(sql);
            }
            return sql;
        }
        return sql;
    }
	

}
