package service;

import model.Course;
import model.Student;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.stream.Collectors;

/**
 * 轻量级 Excel (XLSX) 导出工具，不依赖第三方库。
 * 仅生成一个工作表，包含表头与数据行，无样式、公式、合并单元格等。
 * 支持字符串与数字单元格，字符串使用 inlineStr 存储。
 */
public class ExcelExporter {
    private static final String XML_DECL = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

    /**
     * 导出指定专业排名到 XLSX
     */
    public static boolean exportMajorRankingXlsx(StudentManager studentManager, String major, File file) {
        List<Student> students = studentManager.getStudentsByMajorRanked(major);
        if (students.isEmpty()) return false;
        return exportRankingXlsx(students, major + "专业排名", file);
    }

    /**
     * 导出全体学生按 GPA 排名到 XLSX
     */
    public static boolean exportAllRankingXlsx(StudentManager studentManager, File file) {
        List<Student> students = new ArrayList<>(studentManager.getAllStudents());
        students.sort((a,b) -> Double.compare(b.getGpa(), a.getGpa()));
        return exportRankingXlsx(students, "全部学生排名", file);
    }

    private static boolean exportRankingXlsx(List<Student> students, String sheetTitle, File file) {
        // 收集所有课程名（出现顺序保持）
        LinkedHashSet<String> courseNames = new LinkedHashSet<>();
        for (Student s : students) {
            if (s.getCourses() != null) {
                for (Course c : s.getCourses()) {
                    courseNames.add(c.getCourseName());
                }
            }
        }
        // 计算并列排名
        Map<String,Integer> rankMap = new HashMap<>();
        double prevGpa = -1;
        int rank = 1;
        int actualRank = 1;
        for (Student s : students) {
            if (s.getGpa() != prevGpa) {
                actualRank = rank;
                prevGpa = s.getGpa();
            }
            rankMap.put(s.getStudentId(), actualRank);
            rank++;
        }
        // 表头
        List<String> headers = new ArrayList<>(Arrays.asList(
                "排名","学号","姓名","性别","年龄","专业","班级","联系电话","总学分","课程数"));
        headers.addAll(courseNames);
        headers.add("加权平均分(GPA)");

        ensureParent(file);
        try (OutputStream fos = new FileOutputStream(file); ZipOutputStream zos = new ZipOutputStream(fos)) {
            // [Content_Types].xml
            writeEntry(zos, "[Content_Types].xml", contentTypes());
            // _rels/.rels
            writeEntry(zos, "_rels/.rels", relsRoot());
            // docProps/core.xml & app.xml
            writeEntry(zos, "docProps/core.xml", coreProps());
            writeEntry(zos, "docProps/app.xml", appProps());
            // workbook + rels
            writeEntry(zos, "xl/workbook.xml", workbook());
            writeEntry(zos, "xl/_rels/workbook.xml.rels", workbookRels());
            // worksheet
            writeEntry(zos, "xl/worksheets/sheet1.xml", sheetXml(headers, students, courseNames, rankMap));
            // styles (minimal)
            writeEntry(zos, "xl/styles.xml", styles());
            zos.finish();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void writeEntry(ZipOutputStream zos, String path, String content) throws IOException {
        ZipEntry entry = new ZipEntry(path);
        zos.putNextEntry(entry);
        zos.write(content.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    private static String contentTypes() {
        return XML_DECL +
                "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">" +
                "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>" +
                "<Default Extension=\"xml\" ContentType=\"application/xml\"/>" +
                "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>" +
                "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>" +
                "<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>" +
                "<Override PartName=\"/docProps/core.xml\" ContentType=\"application/vnd.openxmlformats-package.core-properties+xml\"/>" +
                "<Override PartName=\"/docProps/app.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.extended-properties+xml\"/>" +
                "</Types>";
    }

    private static String relsRoot() {
        return XML_DECL +
                "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">" +
                "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>" +
                "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties\" Target=\"docProps/core.xml\"/>" +
                "<Relationship Id=\"rId3\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties\" Target=\"docProps/app.xml\"/>" +
                "</Relationships>";
    }

    private static String coreProps() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestamp = df.format(new Date());
        return XML_DECL +
                "<cp:coreProperties xmlns:cp=\"http://schemas.openxmlformats.org/package/2006/metadata/core-properties\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:dcmitype=\"http://purl.org/dc/dcmitype/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "<dc:title>Ranking Export</dc:title>" +
                "<dc:creator>StudentSystem</dc:creator>" +
                "<cp:lastModifiedBy>StudentSystem</cp:lastModifiedBy>" +
                "<dcterms:created xsi:type=\"dcterms:W3CDTF\">" + timestamp + "</dcterms:created>" +
                "<dcterms:modified xsi:type=\"dcterms:W3CDTF\">" + timestamp + "</dcterms:modified>" +
                "</cp:coreProperties>";
    }

    private static String appProps() {
        return XML_DECL +
                "<Properties xmlns=\"http://schemas.openxmlformats.org/officeDocument/2006/extended-properties\" xmlns:vt=\"http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes\">" +
                "<Application>StudentSystem</Application>" +
                "<DocSecurity>0</DocSecurity><ScaleCrop>false</ScaleCrop>" +
                "<Company></Company><LinksUpToDate>false</LinksUpToDate><SharedDoc>false</SharedDoc>" +
                "<HyperlinksChanged>false</HyperlinksChanged><AppVersion>1.0</AppVersion>" +
                "</Properties>";
    }

    private static String workbook() {
        return XML_DECL +
                "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">" +
                "<sheets><sheet name=\"Ranking\" sheetId=\"1\" r:id=\"rId1\"/></sheets>" +
                "</workbook>";
    }

    private static String workbookRels() {
        return XML_DECL +
                "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">" +
                "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>" +
                "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>" +
                "</Relationships>";
    }

    private static String styles() {
        return XML_DECL +
                "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">" +
                "<numFmts count=\"0\"/>" +
                "<fonts count=\"1\"><font><sz val=\"11\"/><name val=\"Calibri\"/></font></fonts>" +
                "<fills count=\"2\">" +
                "<fill><patternFill patternType=\"none\"/></fill>" +
                "<fill><patternFill patternType=\"gray125\"/></fill>" +
                "</fills>" +
                "<borders count=\"1\"><border><left/><right/><top/><bottom/><diagonal/></border></borders>" +
                "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>" +
                "<cellXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/></cellXfs>" +
                "</styleSheet>";
    }

    private static String sheetXml(List<String> headers, List<Student> students, LinkedHashSet<String> courseNames, Map<String,Integer> rankMap) {
        StringBuilder sb = new StringBuilder();
        sb.append(XML_DECL);
        sb.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">");

        // 添加 dimension 元素
        int totalRows = students.size() + 1;
        int totalCols = headers.size();
        String endCell = columnName(totalCols - 1) + totalRows;
        sb.append("<dimension ref=\"A1:").append(endCell).append("\"/>");

        sb.append("<sheetData>");
        int rowIndex = 1;
        // Header row
        sb.append(rowXml(rowIndex++, headers));
        // Data rows
        for (Student s : students) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(rankMap.get(s.getStudentId())));
            row.add(s.getStudentId());
            row.add(s.getName());
            row.add(s.getGender());
            row.add(String.valueOf(s.getAge()));
            row.add(s.getMajor());
            row.add(s.getClassNumber());
            row.add(s.getPhoneNumber());
            row.add(String.format(Locale.CHINA, "%.1f", s.getTotalCredits()));
            row.add(String.valueOf(s.getCourseCount()));
            Map<String, Course> courseMap = s.getCourses() == null ? Collections.emptyMap() : s.getCourses().stream().collect(Collectors.toMap(Course::getCourseName, c -> c, (a,b)->a));
            for (String cname : courseNames) {
                Course c = courseMap.get(cname);
                row.add(c == null ? "" : String.format(Locale.CHINA, "%.1f", c.getScore()));
            }
            row.add(String.format(Locale.CHINA, "%.2f", s.getGpa()));
            sb.append(rowXml(rowIndex++, row));
        }
        sb.append("</sheetData></worksheet>");
        return sb.toString();
    }

    private static String rowXml(int rowIndex, List<String> cells) {
        StringBuilder sb = new StringBuilder();
        sb.append("<row r=\"" ).append(rowIndex).append("\">");
        for (int i = 0; i < cells.size(); i++) {
            String cellValue = cells.get(i);
            String cellRef = columnName(i) + rowIndex;
            if (isNumeric(cellValue)) {
                sb.append("<c r=\"" ).append(cellRef).append("\" t=\"n\"><v>").append(cellValue).append("</v></c>");
            } else {
                sb.append("<c r=\"" ).append(cellRef).append("\" t=\"inlineStr\"><is><t>")
                  .append(escapeXml(cellValue)).append("</t></is></c>");
            }
        }
        sb.append("</row>");
        return sb.toString();
    }

    private static boolean isNumeric(String v) {
        if (v == null || v.isEmpty()) return false;
        try { Double.parseDouble(v); return true; } catch (NumberFormatException e) { return false; }
    }

    private static String columnName(int index) {
        StringBuilder sb = new StringBuilder();
        int i = index;
        do {
            int rem = i % 26;
            sb.insert(0, (char)('A' + rem));
            i = i / 26 - 1;
        } while (i >= 0);
        return sb.toString();
    }

    private static String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private static void ensureParent(File f) {
        File p = f.getParentFile();
        if (p != null && !p.exists()) p.mkdirs();
    }
}
