package de.elbe5.page.html;

import de.elbe5.page.PageData;
import de.elbe5.response.IHtmlBuilder;

import java.util.Map;

public interface DraftPageWrapper extends IHtmlBuilder {

    default void appendStartHtml(StringBuilder sb, PageData contentData){
        append(sb,"""
                <form action="/ctrl/page/saveContentFrontend/$id$" method="post" id="pageform" name="pageform" accept-charset="UTF-8">
                        <div class="btn-group btn-group-sm pageEditButtons">
                            <button type="submit" class="btn btn-sm btn-success" onclick="updateEditors();">$save$</button>
                            <button class="btn btn-sm btn-secondary" onclick="return linkTo('/ctrl/page/cancelEditContentFrontend/$id$');">$cancel$</button>
                        </div>
                """,
                Map.ofEntries(
                        param("id",contentData.getId()),
                        param("save","_savePage"),
                        param("cancel","_cancel")
                )
        );
    }

    default void appendEndHtml(StringBuilder sb){
        append(sb, """
                    </form>
                """);
    }

    default void appendScript(StringBuilder sb, PageData pageData){
        append(sb,"""
                    <script type="text/javascript">
                        function confirmDelete() {
                            return confirm('$confirm$');
                        }
                        function movePart(id,direction){
                            let $partWrapper=$('#part_'+id);
                            if (direction===1){
                                let $nextPart=$partWrapper.next();
                                if (!$nextPart || $nextPart.length===0){
                                    return false;
                                }
                                $partWrapper.detach();
                                $nextPart.after($partWrapper);
                            }
                            else{
                                let $prevPart=$partWrapper.prev();
                                if (!$prevPart || $prevPart.length===0){
                                    return false;
                                }
                                $partWrapper.detach();
                                $prevPart.before($partWrapper);
                            }
                            updatePartPositions();
                            return false;
                        }
                        function deletePart(id){
                            let $partWrapper=$('#part_'+id);
                            $partWrapper.remove();
                            updatePartPositions();
                            return false;
                        }
                        function addPart(fromId, section, type, layout){
                            let data = {
                                fromPartId: fromId,
                                sectionName: section,
                                partType: type,
                                layout: layout
                            };
                            $.ajax({
                                url: '/ctrl/page/addPart/'+$id$,
                                type: 'POST',
                                data: data,
                                dataType: 'html'
                            }).success(function (html, textStatus) {
                                if (fromId === -1) {
                                    let $section=$('#pageform').find('#section_'+section);
                                    $section.append(html);
                                }
                                else{
                                    let $fromPartWrapper = $('#part_' + fromId);
                                    if ($fromPartWrapper) {
                                        $fromPartWrapper.after(html);
                                    }
                                }
                                updatePartPositions();
                            });
                            return false;
                        }
                        function updateEditors(){
                            if (CKEDITOR) {
                                $(".ckeditField").each(function () {
                                    let id = $(this).attr('id');
                                    $('input[name="' + id + '"]').val(CKEDITOR.instances[id].getData());
                                });
                            }
                        }
                        function updatePartEditors($part){
                            if (CKEDITOR) {
                                $(".ckeditField",$part).each(function () {
                                    let id = $(this).attr('id');
                                    $('input[name="' + id + '"]').val(CKEDITOR.instances[id].getData());
                                });
                            }
                        }
                        function updatePartPositions(){
                            let $sections=$('#pageform').find('.section');
                            $sections.each(function(){
                                updateSectionPartPositions($(this));
                            });
                        }
                        function updateSectionPartPositions($section){
                            let $inputs = $section.find('input.partPos');
                            $inputs.each(function (index) {
                                $(this).attr('value', index);
                            });
                        }
                        updatePartPositions();
                    </script>
                """,
                Map.ofEntries(
                        param("confirm",getJs("_confirmDelete")),
                        param("id",pageData.getId())
                )
        );
    }
}
