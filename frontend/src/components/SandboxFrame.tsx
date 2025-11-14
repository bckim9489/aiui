import { useCallback, useEffect, useRef, useState } from "react";

type SandboxFrameProps = {
  code: string;
};

const MESSAGE_TYPE = "run-code";

export function SandboxFrame({ code }: SandboxFrameProps) {
  const iframeRef = useRef<HTMLIFrameElement | null>(null);
  const [isReady, setIsReady] = useState(false);

  const postCode = useCallback(() => {
    if (!isReady || !code || !iframeRef.current) {
      return;
    }

    iframeRef.current.contentWindow?.postMessage(
      { type: MESSAGE_TYPE, code },
      "*",
    );
  }, [code, isReady]);

  useEffect(() => {
    postCode();
  }, [postCode]);

  return (
    <iframe
      ref={iframeRef}
      className="sandbox-frame"
      title="AI generated UI"
      src="/sandbox.html"
      sandbox="allow-scripts"
      onLoad={() => setIsReady(true)}
    />
  );
}
