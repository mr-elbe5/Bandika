package de.elbe5.response;

import de.elbe5.request.RequestData;

import java.util.Map;

public class ModalPage extends HtmlResponse implements IHtmlBuilder {

    public ModalPage createHtml(RequestData rdata) {
        appendHtml(rdata);
        return this;
    }

    protected void appendHtml(RequestData rdata) {

    }

    static final String modalStartHtml = """
            <div class="modal-dialog modal-lg" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                      <h5 class="modal-title">{{title}}
                      </h5>
                      <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                          <span aria-hidden="true">&times;</span>
                      </button>
                    </div>
            """;
    static final String modalBodyStartTitleHtml = """
                    <div class="modal-body">
                        <h3>{{title}}</h3>
            """;
    static final String modalBodyStartHtml = """
                    <div class="modal-body">
            """;
    static final String modalFooter2ButtonsHtml = """
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline-secondary" data-dismiss="modal">{{secondary}}
                        </button>
                        <button type="submit" class="btn btn-primary">{{primary}}
                        </button>
                    </div>
                </div>
            </div>
            """;
    static final String modalFooter1ButtonsHtml = """
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline-secondary" data-dismiss="modal">{{secondary}}
                        </button>
                    </div>
                </div>
            </div>
            """;
    static final String modalEndHtml = """
                </div>
            </div>
            """;

    public void appendModalStart(String title) {
        append(sb, modalStartHtml,
                Map.ofEntries(
                        Map.entry("title", toHtml(title))));
    }

    public void appendModalBodyStart(String title) {
        append(sb, modalBodyStartTitleHtml,
                Map.ofEntries(
                        Map.entry("title", toHtml(title))));
    }

    public void appendModalBodyStart() {
        append(sb, modalBodyStartHtml);
    }

    public void appendModalFooter(String secondary, String primary) {
        append(sb, modalFooter2ButtonsHtml,
                Map.ofEntries(
                        Map.entry("secondary", toHtml(secondary)),
                        Map.entry("primary", toHtml(primary))));
    }

    public void appendModalFooter(String secondary) {
        append(sb, modalFooter1ButtonsHtml,
                Map.ofEntries(
                        Map.entry("secondary", toHtml(secondary))));
    }

    public void appendModalEnd() {
        append(sb, modalEndHtml);
    }

}
