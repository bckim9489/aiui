package com.aiui.project.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ui")
public class UiCodeController {

    public record UiCodeRequest(String prompt) {
    }

    public record UiCodeResponse(String code) {
    }

    // 재고 대시보드 예시 UI
    private static final String INVENTORY_PAGE_CODE = """
import React, { useEffect, useMemo, useState } from "react";

const styles = {
  root: {
    height: "100%",
    padding: 24,
    boxSizing: "border-box",
    fontFamily: "-apple-system,BlinkMacSystemFont,system-ui,Segoe UI,sans-serif",
    background: "radial-gradient(circle at top, #f5f7ff 0%, #ffffff 55%)",
  },
  shell: {
    maxWidth: 960,
    margin: "0 auto",
    display: "flex",
    flexDirection: "column",
    gap: 16,
    height: "100%",
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  headerTitle: {
    margin: 0,
    fontSize: 20,
    fontWeight: 600,
  },
  headerLabel: {
    margin: 0,
    fontSize: 12,
    color: "#667085",
    textTransform: "uppercase",
    letterSpacing: 0.08,
  },
  badge: {
    fontSize: 12,
    padding: "4px 10px",
    borderRadius: 999,
    border: "1px solid #e4e7ec",
    background: "#f9fafb",
    color: "#475467",
  },
  filterBar: {
    padding: 16,
    borderRadius: 16,
    border: "1px solid #e4e7ec",
    background: "white",
    display: "flex",
    gap: 12,
    alignItems: "center",
    flexWrap: "wrap",
  },
  searchInput: {
    flex: 1,
    minWidth: 200,
    borderRadius: 999,
    border: "1px solid #d0d5dd",
    padding: "8px 14px",
    fontSize: 14,
    outline: "none",
  },
  toggleLabel: {
    display: "flex",
    alignItems: "center",
    gap: 6,
    fontSize: 13,
    color: "#475467",
    whiteSpace: "nowrap",
  },
  tableCard: {
    flex: 1,
    borderRadius: 16,
    border: "1px solid #e4e7ec",
    background: "white",
    overflow: "hidden",
    display: "flex",
    flexDirection: "column",
  },
  table: {
    width: "100%",
    borderCollapse: "collapse",
    fontSize: 13,
  },
  tableHead: {
    background: "#f9fafb",
    borderBottom: "1px solid #e4e7ec",
  },
  th: {
    textAlign: "left",
    padding: "10px 16px",
    color: "#475467",
    fontWeight: 500,
  },
  thRight: {
    textAlign: "right",
    padding: "10px 16px",
    color: "#475467",
    fontWeight: 500,
  },
  td: {
    padding: "10px 16px",
    color: "#101828",
  },
  tdRight: {
    padding: "10px 16px",
    color: "#101828",
    textAlign: "right",
  },
  row: {
    borderBottom: "1px solid #f2f4f7",
  },
  emptyState: {
    flex: 1,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    color: "#667085",
    fontSize: 14,
    padding: 24,
    textAlign: "center",
  },
  centered: {
    flex: 1,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    color: "#667085",
    fontSize: 14,
  },
};

export default function Page({ api }) {
  const [items, setItems] = useState([]);
  const [query, setQuery] = useState("");
  const [onlyLowStock, setOnlyLowStock] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      setError("");
      try {
        const data = await api.get("/api/inventory");
        if (!cancelled) {
          setItems(Array.isArray(data) ? data : []);
        }
      } catch (e) {
        if (!cancelled) {
          setError("재고 정보를 불러오지 못했습니다.");
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, [api]);

  const filtered = useMemo(() => {
    return items
      .filter((item) => {
        if (!query.trim()) return true;
        const name = String(item && item.name ? item.name : "");
        return name.toLowerCase().includes(query.trim().toLowerCase());
      })
      .filter((item) => {
        if (!onlyLowStock) return true;
        const stock = Number(item && item.stock != null ? item.stock : 0);
        return stock <= 5;
      });
  }, [items, query, onlyLowStock]);

  const lowCount = useMemo(
    () =>
      items.filter((item) => {
        const stock = Number(item && item.stock != null ? item.stock : 0);
        return stock <= 5;
      }).length,
    [items]
  );

  return (
    <div style={styles.root}>
      <div style={styles.shell}>
        <header style={styles.header}>
          <div>
            <p style={styles.headerLabel}>Inventory</p>
            <h1 style={styles.headerTitle}>재고 현황 대시보드</h1>
          </div>
          <div style={{ textAlign: "right" }}>
            <div style={styles.badge}>총 {items.length}개 품목</div>
            {items.length > 0 && (
              <div style={{ marginTop: 4, fontSize: 11, color: "#667085" }}>
                재고 5개 이하: {lowCount}개
              </div>
            )}
          </div>
        </header>

        <section style={styles.filterBar}>
          <input
            style={styles.searchInput}
            placeholder="품목명으로 검색해 보세요"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
          <label style={styles.toggleLabel}>
            <input
              type="checkbox"
              checked={onlyLowStock}
              onChange={(e) => setOnlyLowStock(e.target.checked)}
            />
            재고 5개 이하만 보기
          </label>
        </section>

        <section style={styles.tableCard}>
          {loading ? (
            <div style={styles.centered}>재고 데이터를 불러오는 중입니다...</div>
          ) : error ? (
            <div style={{ ...styles.centered, color: "#b42318" }}>{error}</div>
          ) : filtered.length === 0 ? (
            <div style={styles.emptyState}>
              조건에 맞는 재고가 없습니다.
            </div>
          ) : (
            <table style={styles.table}>
              <thead style={styles.tableHead}>
                <tr>
                  <th style={styles.th}>ID</th>
                  <th style={styles.th}>품목명</th>
                  <th style={styles.thRight}>재고 수량</th>
                  <th style={styles.thRight}>상태</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((item, index) => {
                  const stock = Number(
                    item && item.stock != null ? item.stock : 0
                  );
                  const low = stock <= 5;
                  return (
                    <tr key={item && item.id != null ? item.id : index} style={styles.row}>
                      <td style={styles.td}>{item && item.id}</td>
                      <td style={styles.td}>{item && item.name}</td>
                      <td style={styles.tdRight}>{stock}</td>
                      <td style={styles.tdRight}>
                        <span
                          style={{
                            display: "inline-flex",
                            alignItems: "center",
                            padding: "2px 10px",
                            borderRadius: 999,
                            fontSize: 11,
                            fontWeight: 500,
                            backgroundColor: low ? "#fef3f2" : "#ecfdf3",
                            color: low ? "#b42318" : "#027a48",
                            border: low ? "1px solid #fecdcf" : "1px solid #a6f4c5",
                          }}
                        >
                          {low ? "재고 부족" : "정상"}
                        </span>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          )}
        </section>
      </div>
    </div>
  );
}
""";

    // 비밀번호 변경 페이지 예시 UI
    private static final String PASSWORD_PAGE_CODE = """
import React, { useState } from "react";

const styles = {
  root: {
    height: "100%',
    padding: 24,
    boxSizing: "border-box",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontFamily: "-apple-system,BlinkMacSystemFont,system-ui,Segoe UI,sans-serif",
    background: "radial-gradient(circle at top, #eef2ff 0%, #ffffff 55%)",
  },
  card: {
    width: 420,
    maxWidth: "100%",
    borderRadius: 20,
    border: "1px solid #e4e7ec",
    background: "white",
    padding: 24,
    boxShadow:
      "0 20px 40px rgba(15, 23, 42, 0.08), inset 0 1px 0 rgba(255,255,255,0.8)",
  },
  title: {
    margin: 0,
    fontSize: 20,
    fontWeight: 600,
  },
  subtitle: {
    margin: "6px 0 20px",
    fontSize: 13,
    color: "#667085",
  },
  field: {
    display: "flex",
    flexDirection: "column",
    gap: 4,
  },
  label: {
    fontSize: 13,
    fontWeight: 500,
    color: "#344054",
  },
  input: {
    borderRadius: 10,
    border: "1px solid #d0d5dd",
    padding: "9px 11px",
    fontSize: 14,
  },
  hint: {
    margin: 0,
    fontSize: 11,
    color: "#667085",
  },
  message: {
    margin: "4px 0 0",
    fontSize: 12,
  },
  button: {
    marginTop: 10,
    border: "none",
    height: 40,
    borderRadius: 999,
    fontWeight: 600,
    fontSize: 14,
    cursor: "pointer",
  },
};

export default function Page({ api }) {
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState("info");

  const canSubmit =
    currentPassword.trim().length > 0 &&
    newPassword.trim().length >= 8 &&
    newPassword === confirm &&
    !submitting;

  async function handleSubmit(e) {
    e.preventDefault();
    if (!canSubmit) return;

    setSubmitting(true);
    setMessage("");
    try {
      await api.post("/api/me/change-password", {
        currentPassword,
        newPassword,
      });
      setMessageType("success");
      setMessage("비밀번호가 성공적으로 변경되었습니다.");
      setCurrentPassword("");
      setNewPassword("");
      setConfirm("");
    } catch (err) {
      setMessageType("error");
      setMessage("비밀번호 변경에 실패했습니다. 현재 비밀번호를 확인해 주세요.");
    } finally {
      setSubmitting(false);
    }
  }

  const passwordMismatch =
    newPassword && confirm && newPassword !== confirm
      ? "새 비밀번호가 서로 일치하지 않습니다."
      : "";

  const buttonStyle = {
    ...styles.button,
    background: canSubmit
      ? "linear-gradient(135deg, #7b5dff, #5de0e6)"
      : "#e4e7ec",
    color: canSubmit ? "#ffffff" : "#98a2b3",
    boxShadow: canSubmit ? "0 12px 24px rgba(123, 93, 255, 0.3)" : "none",
    cursor: canSubmit ? "pointer" : "not-allowed",
  };

  let messageColor = "#475467";
  if (messageType === "success") messageColor = "#027a48";
  if (messageType === "error") messageColor = "#b42318";

  return (
    <div style={styles.root}>
      <div style={styles.card}>
        <h1 style={styles.title}>비밀번호 변경</h1>
        <p style={styles.subtitle}>
          계정 보안을 위해 정기적으로 비밀번호를 변경해 주세요.
        </p>

        <form
          onSubmit={handleSubmit}
          style={{ display: "flex", flexDirection: "column", gap: 14 }}
        >
          <div style={styles.field}>
            <label style={styles.label}>현재 비밀번호</label>
            <input
              type="password"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              style={styles.input}
            />
          </div>

          <div style={styles.field}>
            <label style={styles.label}>새 비밀번호</label>
            <input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              style={styles.input}
            />
            <p style={styles.hint}>영문/숫자/특수문자 포함 8자 이상</p>
          </div>

          <div style={styles.field}>
            <label style={styles.label}>새 비밀번호 확인</label>
            <input
              type="password"
              value={confirm}
              onChange={(e) => setConfirm(e.target.value)}
              style={styles.input}
            />
          </div>

          {message && (
            <p style={{ ...styles.message, color: messageColor }}>{message}</p>
          )}

          {passwordMismatch && (
            <p style={{ ...styles.message, color: "#b42318" }}>
              {passwordMismatch}
            </p>
          )}

          <button type="submit" disabled={!canSubmit} style={buttonStyle}>
            {submitting ? "변경 중..." : "비밀번호 변경"}
          </button>
        </form>
      </div>
    </div>
  );
}
""";

    @PostMapping("/code")
    public ResponseEntity<UiCodeResponse> generate(@RequestBody UiCodeRequest request) {
        String prompt = request != null && request.prompt() != null ? request.prompt().toLowerCase() : "";

        String code;
        if (prompt.contains("재고") || prompt.contains("inventory")) {
            code = INVENTORY_PAGE_CODE;
        } else if (prompt.contains("비밀번호") || prompt.contains("password")) {
            code = PASSWORD_PAGE_CODE;
        } else {
            code = "";
        }

        return ResponseEntity.ok(new UiCodeResponse(code));
    }
}

