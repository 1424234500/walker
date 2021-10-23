
-- 建表语句 ${tableNameChinese} : ${tableInfo}
DROP TABLE if EXISTS `${tableName}`;

CREATE TABLE `${tableName}` (


        String key = primaryKey.tableColumnName;
        StringBuilder cs = new StringBuilder();
        StringBuilder insertK = new StringBuilder();
        StringBuilder insertV = new StringBuilder();
        for(int i = 0; i < this.columnList.size(); i++) {
            Column column = this.columnList.get(i);
            if (column.isTableColumnInfo.length() == 0) {
                cs.append("\n").append("    " + column.tableColumnName + ", ");
                sb.append("\n").append("    `" + column.tableColumnName + "` " + column.tableColumnType);
                if(column.queryEg.length() > 0){
                    insertK.append("\n").append("    " + column.tableColumnName + ", ");
                    insertV.append("\n").append("    " + column.queryEg + ", ");
                }
                if (column.notNull.equals("1")) {
                    sb.append(" NOT NULL ");
                } else if (column.notNull.equals("2")) {
                    sb.append(" AUTO_INCREMENT ");
                    key = column.tableColumnName;   //自增优先单主键
                } else if (column.notNull.equals("3")) {

                }
                if (column.defValue.length() > 0) {
                    sb.append(" DEFAULT " + column.defValue);
                }
                if (column.nameChinese.length() > 0) {
                    sb.append(" COMMENT '" + column.nameChinese + "'");
                }
                sb.append(", ");

            }
        }

        sb.append("\n\n    PRIMARY KEY (`" + key + "`), ");
        if (!key.equals(primaryKey.tableColumnName)) { //额外逻辑主键索引
            sb.append("\n    KEY `IDX_" + primaryKey.tableColumnName + "` (`" + primaryKey.tableColumnName + "`), ");
        }
        sb.setLength(sb.length() - ", ".length());
        cs.setLength(cs.length() - ", ".length());
        insertK.setLength(insertK.length() - ", ".length());
        insertV.setLength(insertV.length() - ", ".length());

        sb.append("\n) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='" + this.tableNameChinese + "'").append("\n");
        sb.append("; ").append("\n\n");

        sb.append("\n-- 清空表参考sql " + this.tableNameChinese + " \n");
        sb.append("TRUNCATE TABLE `" + this.tableName + "`; \n");

        sb.append("\n-- 样例数据初始化sql " + this.tableNameChinese + " \n");
        String insertSql = "insert into " + this.tableName + "(" + insertK + "\n) values (" + insertV + "\n); ";
        insertSql = insertSql.replace("\n", " ").replaceAll("  +", " ");
        sb.append(insertSql + "\n");

        sb.append("\n-- 查询表参考sql " + this.tableNameChinese + " \n");
        sb.append("select " + cs + " \nfrom " + this.tableName + "; \n");

        Tools.out("\n" + sb);
        this.sqlDdlDml = sb.toString();