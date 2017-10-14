package csnowstack.generate;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 生成 screenOrientation属性的插件
 */
public class GenerateAction extends AnAction {





    @Override
    public void actionPerformed(AnActionEvent e) {
        //获取编辑器中的文件
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        String selectText = editor.getSelectionModel().getSelectedText();

        Project project = e.getData(PlatformDataKeys.PROJECT);
        Document document = editor.getDocument();

        SelectionModel selectionModel = editor.getSelectionModel();



        //匹配<activityXXXX>
        Pattern pattern = Pattern.compile("<activity[^>]*>");
        Matcher m = pattern.matcher(selectText);

        //匹配android:name=""
        Pattern patternActivity = Pattern.compile("android:name=\"[\\S^\"]*\"");

        //需要设置竖屏的activity
        List<String> shouldReplaceList = new ArrayList<>();
        while (m.find()) {
            String find = m.group();

            if (!find.contains("android:screenOrientation=")) {
                Matcher activityMatch = patternActivity.matcher(find);

                if (activityMatch.find()) {
                    shouldReplaceList.add(activityMatch.group());
                }
            }
        }
        //增加竖屏的属性
        for (String activity : shouldReplaceList) {
            selectText=  selectText.replace(activity,activity+"\n\t\t\tandroid:screenOrientation=\"portrait\"");
        }

        String finalSelectText = selectText;
        Runnable runnable =()-> document.replaceString(selectionModel.getSelectionStart(),
                selectionModel.getSelectionEnd(), finalSelectText);

        WriteCommandAction.runWriteCommandAction(project, runnable);
        selectionModel.removeSelection();
    }


    /**
     * 弹框打印log
     */
    public void showDialog(String  text) {
        Messages.showMessageDialog("测试", text, Messages.getInformationIcon());
    }
}
